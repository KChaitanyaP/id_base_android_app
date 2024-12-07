# id_base_android_app
Inner dialogue Base - contains the start and main activities of ID

## Step by step prodedure to getting this app connected to GCP
1. Open Google account and Google Cloud account.
2. Navigate to Firebase
   1. Create a new project in Firebase console
   2. Create RTDB
   3. Enable a basic sign in method like email/password
3. Add Firebase to this repo using the steps in https://firebase.google.com/docs/android/setup
4. ```export GOOGLE_APPLICATION_CREDENTIALS=<Firebase service account key json file path>```

### Resources
Most of the base adapters, fragments and layouts are created based on https://youtu.be/Dl_IFwldjWk?si=eg2vcfPmSBk1Hf7e