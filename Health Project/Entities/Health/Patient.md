The patient entity is the core of the application. It holds all patient identifying data, and will be used with external services to connect to files (such as PDFs, images, etc.).

___
## Properties
 - Patient ID
	 - Auto generated value to identify the patient on DB.
 - Name
 - Surname
 - Full Name
	 - Needed for search purposes
 - Contact phone
	 - Phone to contact concerning the patient
 - Owner ID
	 - The [[User]] who added the patient an therefore has admin rights of the patient inside the app
 - Gender
	 - Male
	 - Female
	 - Unknown
	 - Not Specified
 - RG
 - CPF
 - Date of birth
 - Create Date
 - Update Date