package com.example.substracker.Service;

import com.example.substracker.Model.ExpirationAlert;
import com.example.substracker.Model.Subscription;
import com.example.substracker.Model.User;
import com.example.substracker.Repository.ExpirationAlertRepository;
import com.example.substracker.Repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpirationAlertService {

    private final ExpirationAlertRepository expirationAlertRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final NotificationService notificationService;

    /**
     * Send expiration alert email to user
     */
    public void sendExpirationAlert(ExpirationAlert alert) {
        try {
            Subscription subscription = alert.getSubscription();
            User user = subscription.getUser();

            if (!user.getEmailNotificationsEnabled()) {
                System.out.println("Email notifications disabled for user: " + user.getEmail());
                return;
            }

            String subject = buildEmailSubject(alert.getAlertType(), subscription.getSubscriptionName());
            String htmlBody = buildExpirationEmailHtml(
                    alert.getAlertType(),
                    user.getName(),
                    subscription.getSubscriptionName(),
                    subscription.getNextBillingDate().toString(),
                    alert.getDaysBeforeExpiry(),
                    subscription.getPrice(),
                    subscription.getUrl() // Add the subscription URL
            );

            notificationService.sendHtmlEmail(user.getEmail(), subject, htmlBody);
            System.out.println("Expiration alert sent to: " + user.getEmail());

            // بعد الإرسال عدّل الحالة واحفظ
            alert.setIsSent(true);
            expirationAlertRepository.save(alert);

        } catch (Exception e) {
            System.err.println("Failed to send expiration alert: " + e.getMessage());
        }
    }

    /**
     * Create and save expiration alert
     */
    public ExpirationAlert createExpirationAlert(Subscription subscription, String alertType, int daysBeforeExpiry) {
        ExpirationAlert alert = new ExpirationAlert();
        alert.setSubscription(subscription);
        alert.setAlertType(alertType);
        alert.setDaysBeforeExpiry(daysBeforeExpiry);
        alert.setAlertDate(LocalDateTime.now());

        String message = buildAlertMessage(alertType, subscription.getSubscriptionName(), daysBeforeExpiry);
        alert.setMessage(message);
        alert.setIsSent(false); // أول ما ينشأ يكون false

        return expirationAlertRepository.save(alert);
    }

    /**
     * Check subscriptions and send alerts - runs daily at 9 AM
     */
    @Scheduled(cron = "0 * * * * *")
    public void checkAndSendExpirationAlerts() {
        System.out.println("Checking for subscription expirations...");

        LocalDate today = LocalDate.now();
        List<Subscription> activeSubscriptions = subscriptionRepository.findByStatus("Active");

        for (Subscription subscription : activeSubscriptions) {
            User user = subscription.getUser();

            if (user == null || !Boolean.TRUE.equals(user.getEmailNotificationsEnabled())) {
                continue;
            }

            LocalDate nextBillingDate = subscription.getNextBillingDate();
            int daysDiff = (int) java.time.temporal.ChronoUnit.DAYS.between(today, nextBillingDate);

            if (daysDiff == 7 || daysDiff == 2) {
                String alertType = (daysDiff == 7) ? "normal" : "urgent";

                boolean alreadySent = expirationAlertRepository
                        .existsBySubscriptionAndAlertTypeAndIsSent(subscription, alertType, true);

                if (!alreadySent) {
                    ExpirationAlert alert = createExpirationAlert(subscription, alertType, daysDiff);
                    sendExpirationAlert(alert);
                }
            }
        }

        System.out.println("Expiration check completed.");
    }

    /**
     * Manual trigger for sending alerts for a specific subscription
     */
    public void sendManualAlert(Integer subscriptionId, String alertType) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));

        int daysBeforeExpiry = alertType.equals("urgent") ? 2 : 7;

        boolean alreadySent = expirationAlertRepository
                .existsBySubscriptionAndAlertTypeAndIsSent(subscription, alertType, true);

        if (!alreadySent) {
            ExpirationAlert alert = createExpirationAlert(subscription, alertType, daysBeforeExpiry);
            sendExpirationAlert(alert);
        }
    }

    /**
     * Build email subject based on alert type
     */
    private String buildEmailSubject(String alertType, String subscriptionName) {
        if ("urgent".equals(alertType)) {
            return "🚨 URGENT: " + subscriptionName + " expires in 2 days!";
        } else {
            return "⏰ Reminder: " + subscriptionName + " expires in 7 days";
        }
    }

    /**
     * Build alert message
     */
    private String buildAlertMessage(String alertType, String subscriptionName, int daysBeforeExpiry) {
        return String.format("Your subscription '%s' will expire in %d days. Please renew to continue service.",
                subscriptionName, daysBeforeExpiry);
    }

    /**
     * Build HTML email template for expiration alerts
     */
    private String buildExpirationEmailHtml(String alertType, String userName, String subscriptionName,
                                            String expirationDate, int daysBeforeExpiry, Double price, String renewUrl) {

        String icon = "urgent".equals(alertType) ? "🚨" : "⏰";
        String alertTitle = "urgent".equals(alertType) ? "URGENT EXPIRATION ALERT" : "SUBSCRIPTION REMINDER";

        // Ensure the URL has a protocol if it doesn't already have one
        String finalRenewUrl = renewUrl;
        if (renewUrl != null && !renewUrl.isEmpty() && !renewUrl.startsWith("http://") && !renewUrl.startsWith("https://")) {
            finalRenewUrl = "https://" + renewUrl;
        }

        return String.format("""
            <!doctype html>
            <html lang="en">
            <head>
              <meta charset="UTF-8" />
              <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
              <title>Subscription Expiration Alert</title>
            </head>
            <body style="margin:0;padding:0;background:#f6f7fb;font-family:Arial,Helvetica,sans-serif;">
              <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="background:#f6f7fb;padding:24px 0;">
                <tr>
                  <td align="center">
                    <table role="presentation" width="600" cellspacing="0" cellpadding="0" style="background:#ffffff;border-radius:12px;overflow:hidden">
                      <tr>
                        <td style="background:%s;color:#fff;padding:20px 24px;font-size:18px;font-weight:700;text-align:center;">
                          %s %s
                        </td>
                      </tr>
                      <tr>
                        <td style="padding:24px;color:#111827;">
                          <p style="margin:0 0 12px 0;font-size:16px;">Hi %s,</p>
                          <p style="margin:0 0 16px 0;font-size:14px;color:#374151">
                            Your subscription <strong>%s</strong> will expire in <strong>%d days</strong> on %s.
                          </p>
                          <div style="background:#f9fafb;border-left:4px solid %s;padding:16px;margin:16px 0;">
                            <p style="margin:0;font-size:14px;color:#374151">
                              <strong>Subscription Details:</strong><br>
                              Name: %s<br>
                              Price: $%.2f<br>
                              Expiration Date: %s
                            </p>
                          </div>
                          <p style="margin:16px 0;font-size:14px;color:#374151">
                            To avoid service interruption, please renew your subscription before the expiration date.
                          </p>
                          <p style="margin:0 0 24px 0;">
                            <a href="%s" style="display:inline-block;background:#2563eb;color:#ffffff;text-decoration:none;padding:12px 24px;border-radius:8px;font-weight:600;" target="_blank">
                              Renew Subscription
                            </a>
                          </p>
                          <p style="margin:0;color:#6b7280;font-size:12px">
                            This is an automated reminder. If you have already renewed, please ignore this message.
                          </p>
                        </td>
                      </tr>
                      <tr>
                        <td style="background:#f3f4f6;padding:16px;color:#6b7280;text-align:center;font-size:12px">
                          © %d SubsTracker - Subscription Management System
                        </td>
                      </tr>
                    </table>
                  </td>
                </tr>
              </table>
            </body>
            </html>
            """,
                "urgent".equals(alertType) ? "#dc2626" : "#2563eb",
                icon,
                alertTitle,
                userName,
                subscriptionName,
                daysBeforeExpiry,
                expirationDate,
                "urgent".equals(alertType) ? "#dc2626" : "#2563eb",
                subscriptionName,
                price,
                expirationDate,
                finalRenewUrl != null && !finalRenewUrl.isEmpty() ? finalRenewUrl : "#",
                java.time.Year.now().getValue()
        );
    }
}