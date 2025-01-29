[![Build QualiTag Java](https://github.com/follen99/QualiTag/actions/workflows/java.yml/badge.svg?branch=main&event=push)](https://github.com/follen99/QualiTag/actions/workflows/java.yml)
[![License](https://img.shields.io/github/license/follen99/QualiTag)](https://github.com/follen99/QualiTag/blob/main/LICENSE)
# QualiTag
GitHub repository dedicated to SoftwareEngineering project exam

## Running the system

### Non-versioned files

1. Download the file *setup.zip*
2. Extract it
3. Copy the content of the extracted folder (setup)  into project root

### Mail service

1. If present, delete the file `qualitag/src/main/resources/credentials/tokens/StoredCredential`
2. Run the main of the file `qualitag/src/main/java/it/unisannio/studenti/qualitag/service/GmailService.java`
3. Click on the link
4. Log in with a google account
5. A folder will be created in `C:\Users\<username>\AppData\Local\Temp\tokens####`
6. Inside the folder will be a file named `StoredCredential`; copy it inside the folder: `qualitag/src/main/resources/credentials/tokens/`.
7. You're good to go.

## System Requirements
A user can connect to the system *only after logging in*; if they do not have an account, they can register using email:password.

After logging in, the user can perform two operations:

1. Create a new project --> when the project is created, the "*ownerId*" field of the project is assigned to the userId of the user who created it.
    1. If a user is associated with the project, they see it in the list of available projects, and if they are the owner, they have access to a dashboard that exposes the following functionalities:
        1. *Terminate* the tagging operation, so that all basic users will no longer see the artifacts.
        2. *Add artifacts* to be tagged to the project. If the tagging operation is complete, no more artifacts can be added to the project.
    2. If the user is not the owner of the project, they can view their projects but instead of seeing the dashboard, they see a list of artifacts to be tagged; the name of each artifact is given by the filename.
        1. Artifacts marked as *tagged* are displayed at the bottom and can no longer be tagged.
        2. Artifacts to be tagged are displayed at the top.
        3. The user can click on an artifact, which opens a screen displaying the text of the artifact.
            1. It is possible to add a new tag associated with the artifact; each tag stores the user who created it: We have a `createdBy` field in the Tag object that stores the user who added it to the artifact.
            2. Tags created to tag an artifact are not also saved for easy access next time. *too complex for a basic implementation*.
            3. *Tag deletion*
                1. The user can *delete* a tag *associated with the artifact* by clicking the x next to the tag content displayed in the list of tags associated with the artifact.