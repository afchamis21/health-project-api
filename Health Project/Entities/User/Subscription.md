The subscription entity holds key subscription data from Stripe.

---
## Properties
 - Subscription ID
	 - The ID of the subscription that Stripe sends us when the subscription is created
 - Stripe Client ID
	 - The ID of the client for Stripe
	 - Connects with [[User]]
 - Cancel At Period End
	 - Whether the subscription is set to automatically renew, or to be cancelled
 - Status
	 - Status of the subscription
 - Create Date
	 - When was the subscription created
 - Update Date
	 - When was the subscription last updated
 - Period Start
	 - When does the billing period of the subscription starts
 - Period End 
	 - When does the billing period of the subscription ends