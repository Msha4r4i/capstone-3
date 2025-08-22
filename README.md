
# SubsTracker - Additional API Endpoints

## Project Overview
SubsTracker is a team-developed subscription management application that helps users track their subscriptions, receive notifications, and manage payments. This document outlines the additional endpoints I've implemented for enhanced functionality.

## My Contribution - Additional Endpoints (By Mshari)

### 1. User Notification Trigger
**Endpoint:** `PUT /api/users/{userId}/notifications/trigger`

**Description:** Manually trigger notifications for a specific user. This endpoint activates the notification system to send alerts about subscription status, upcoming payments, or expired subscriptions to the user.

**Purpose:** 
- Send immediate notifications to users about their subscription status
- Useful for admin operations or when users request manual notification triggers
- Can be used for testing notification systems

**Request Parameters:**
- `userId` (path parameter) - The unique identifier of the user (Integer)

**Response:** Returns a success message indicating notifications were triggered

### 2. Email Existence Checker
**Endpoint:** `GET /api/users/get/email/{email}`

**Description:** Check if a user exists in the database by their email address. Returns a boolean value indicating whether the email is already registered.

**Purpose:**
- Validate email uniqueness during user registration
- Check if a user exists before performing operations
- Useful for "forgot password" functionality validation

**Request Parameters:**
- `email` (path parameter) - The email address to check (String)

**Response:** Returns boolean true if email exists, false if email doesn't exist

### 3. Upcoming Subscriptions for User
**Endpoint:** `GET /api/subscriptions/user/{userId}/upcoming`

**Description:** Retrieve all upcoming subscription payments/renewals for a specific user. Returns subscription data formatted as DTOOut objects.

**Purpose:**
- Display upcoming payments to help users plan their finances
- Send proactive notifications about upcoming charges
- Dashboard overview of future subscription costs

**Request Parameters:**
- `userId` (path parameter) - The unique identifier of the user (Integer)

**Response:** Returns array of upcoming subscriptions with payment dates, amounts, and service details

### 4. Subscriptions Due Within Specific Days
**Endpoint:** `GET /api/subscriptions/user/{userId}/day/{days}`

**Description:** Retrieve subscriptions that are due within a specified number of days for a user. Useful for urgent payment notifications and reminders.

**Purpose:**
- Get subscriptions requiring immediate attention
- Generate urgent payment reminders
- Filter subscriptions by urgency level based on days until due

**Request Parameters:**
- `userId` (path parameter) - The unique identifier of the user (Integer)
- `days` (path parameter) - Number of days to check for due subscriptions (int)

**Response:** Returns array of subscriptions due within the specified timeframe

### 5. Active Subscriptions for User
**Endpoint:** `GET /api/subscriptions/user/{userId}/active`

**Description:** Retrieve all currently active subscriptions for a specific user. Returns active subscription data in DTOOut format.

**Purpose:**
- Dashboard view of all active subscriptions
- Calculate total monthly/yearly subscription costs
- Manage and review current subscription portfolio

**Request Parameters:**
- `userId` (path parameter) - The unique identifier of the user (Integer)

**Response:** Returns array of active subscriptions with current status, payment dates, and billing information

### 6. Expired Subscriptions for User
**Endpoint:** `GET /api/subscriptions/user/{userId}/expired`

**Description:** Retrieve all expired subscriptions for a specific user. Returns expired subscription data in DTOOut format.

**Purpose:**
- View subscription history and expired services
- Identify potential renewal opportunities
- Track subscription lifecycle and patterns

**Request Parameters:**
- `userId` (path parameter) - The unique identifier of the user (Integer)

**Response:** Returns array of expired subscriptions with end dates, last payment amounts, and expiration reasons

## Additional Contribution - PDF Invoice System

### PDF Invoice Generation and Email Delivery
**Description:** I have also participated in creating a comprehensive PDF invoice system that automatically generates and sends payment receipts to users via email when they complete their subscription payments.

**Key Features:**
- **Automatic PDF Generation:** When a user successfully pays for their subscription, the system automatically creates a professional PDF invoice
- **Email Delivery:** The generated PDF invoice is automatically sent to the user's registered email address
- **Professional Design:** Custom-styled invoice template with company branding and detailed payment information
- **Payment Integration:** Integrated with Moyasar payment gateway to verify payment status before generating invoices
- **HTML Email Templates:** Beautifully formatted email notifications with the PDF invoice attached

**Process Flow:**
1. User completes payment for subscription
2. System verifies payment status with payment gateway
3. PDF invoice is automatically generated with user and payment details
4. Professional email with PDF attachment is sent to user's email
5. User receives confirmation email with their subscription receipt

**Invoice Content:**
- Company branding and logo
- Customer information (name, email)
- Subscription details (service, plan, period)
- Payment information (amount, date, status)
- Professional styling with modern design

**Technical Components:**
- **PDF Service:** Builds customized PDF receipts with user data
- **Email Service:** Handles HTML email delivery with PDF attachments
- **Payment Verification:** Integrates with payment gateway for status confirmation
- **Template Engine:** Uses HTML templates for consistent invoice formatting
