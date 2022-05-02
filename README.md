# The Nigma App

![Nigma App](/resources/Githubdisplay.png)

The Nigma App is a puzzle game created with Jetpack compose and bootsted with AppWrite.

## App overview

* You can register a new account by typing in a username, email, and password. The user is saved in the User feature of AppWrite.
* You can log in with your email and password. AppWrite provides the login mechanism.
* The list of puzzles is shown by fetching from the AppWrite database and is watching for changes in real-time.
* You can create a new puzzle by writing a name and description and selecting an image from your storage. Then the database is updated and the image is uploaded to the AppWrite storage feature.
* When you complete a puzzle the database update the corresponding document by adding the userId. When the userId is set, the Realtime feature sends a callback that a puzzle document changes, and then, the list is updated removing the puzzles resolved by the authenticated user.
* Puzzles resolved by the user are shown in the profile section.
* The profile section allows you to: change the username, change the image profile, and log out.

## Build by your own

* Install AppWrite by following the instructions [here](https://appwrite.io/docs/installation).
* Create your user account.
* Create a project called 'nigma-app'
* Create a collection called 'puzzle-collection'.
* Set these properties to the puzzle-collection:
    - name: string
    - description: string
    - puzzles_completed: string[]
    - img_file_id: string
* Create a bucket called 'profile-images'.
* Create a bucket called 'puzzles-imgs'.
* Configure your local.properties file in your Android Project by adding these lines:
    - api.endpoint = https://[YOUR_LOCAL_ADDRESS]/v1
    - app.write.project.name = nigma-app

## About the project

This project is participating in the Appwrite Hackathon on DEV.
Created with 🧡 by Carlos Lopez Romero.
