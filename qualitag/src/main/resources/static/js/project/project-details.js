const artifactsIcon = `<svg xmlns="http://www.w3.org/2000/svg" width="1em" height="1em" fill="currentColor" class="bi bi-card-text" viewBox="0 0 16 16">
                      <path d="M14.5 3a.5.5 0 0 1 .5.5v9a.5.5 0 0 1-.5.5h-13a.5.5 0 0 1-.5-.5v-9a.5.5 0 0 1 .5-.5zm-13-1A1.5 1.5 0 0 0 0 3.5v9A1.5 1.5 0 0 0 1.5 14h13a1.5 1.5 0 0 0 1.5-1.5v-9A1.5 1.5 0 0 0 14.5 2z"/>
                      <path d="M3 5.5a.5.5 0 0 1 .5-.5h9a.5.5 0 0 1 0 1h-9a.5.5 0 0 1-.5-.5M3 8a.5.5 0 0 1 .5-.5h9a.5.5 0 0 1 0 1h-9A.5.5 0 0 1 3 8m0 2.5a.5.5 0 0 1 .5-.5h6a.5.5 0 0 1 0 1h-6a.5.5 0 0 1-.5-.5"/>
                    </svg>`;

const usersIcon = `<svg xmlns="http://www.w3.org/2000/svg" width="1em" height="1em" fill="currentColor" class="bi bi-person" viewBox="0 0 16 16">
                            <path d="M8 8a3 3 0 1 0 0-6 3 3 0 0 0 0 6m2-3a2 2 0 1 1-4 0 2 2 0 0 1 4 0m4 8c0 1-1 1-1 1H3s-1 0-1-1 1-4 6-4 6 3 6 4m-1-.004c-.001-.246-.154-.986-.832-1.664C11.516 10.68 10.289 10 8 10s-3.516.68-4.168 1.332c-.678.678-.83 1.418-.832 1.664z"/>
                        </svg>`;

const teamIcon = `<svg xmlns="http://www.w3.org/2000/svg" width="1em" height="1em" fill="currentColor" class="bi bi-microsoft-teams" viewBox="0 0 16 16">
                          <path d="M9.186 4.797a2.42 2.42 0 1 0-2.86-2.448h1.178c.929 0 1.682.753 1.682 1.682zm-4.295 7.738h2.613c.929 0 1.682-.753 1.682-1.682V5.58h2.783a.7.7 0 0 1 .682.716v4.294a4.197 4.197 0 0 1-4.093 4.293c-1.618-.04-3-.99-3.667-2.35Zm10.737-9.372a1.674 1.674 0 1 1-3.349 0 1.674 1.674 0 0 1 3.349 0m-2.238 9.488-.12-.002a5.2 5.2 0 0 0 .381-2.07V6.306a1.7 1.7 0 0 0-.15-.725h1.792c.39 0 .707.317.707.707v3.765a2.6 2.6 0 0 1-2.598 2.598z"/>
                          <path d="M.682 3.349h6.822c.377 0 .682.305.682.682v6.822a.68.68 0 0 1-.682.682H.682A.68.68 0 0 1 0 10.853V4.03c0-.377.305-.682.682-.682Zm5.206 2.596v-.72h-3.59v.72h1.357V9.66h.87V5.945z"/>
                        </svg>`;

const defaultIcon = '<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-app" viewBox="0 0 16 16">\n'
    + '  <path d="M11 2a3 3 0 0 1 3 3v6a3 3 0 0 1-3 3H5a3 3 0 0 1-3-3V5a3 3 0 0 1 3-3zM5 1a4 4 0 0 0-4 4v6a4 4 0 0 0 4 4h6a4 4 0 0 0 4-4V5a4 4 0 0 0-4-4z"/>\n'
    + '</svg>'

// main control flow
document.addEventListener('DOMContentLoaded', function () {
  // Token validation
  const token = localStorage.getItem('authToken');
  if (!token) {
    alert('You are not logged in. Please log in first.');
    window.location.href = '/signin';
  }

  // Fetch project details
  const urlParams = new URLSearchParams(window.location.search);
  const projectId = urlParams.get('id');

  fetch(`/api/v1/project/${projectId}/status/whole`, {
    method: 'GET',
    headers: {
      'Authorization': `Bearer ${token}`
    }
  })
  .then(response => {
    if (!response.ok) {
      return response.json().then(errorData => {
        // get error message
        console.log("Error message: " + errorData.msg);
        alert(errorData.msg + "\nYou will redirect to the project list page.");
        window.location.href = "/project/" + localStorage.getItem('username')
            + "/projects";
      });
    }
    return response.json(); // Se la risposta è ok, continua con il parsing dei dati
  })
  .then(project => {
    console.log("fetched project" + JSON.stringify(project));

    /**
     * ESEMPIO PROJECT:
     * {"projectName":"TestProject",
     * "projectDescription":"This is an example project description.",
     * "projectCreationDate":1733765196271,
     * "projectDeadline":1733766196271,
     * "ownerId":"67536ef2fa84d0533c3a577d",
     * "projectStatus":"NO_INFO",
     * "users":["67536ef2fa84d0533c3a577c", "67536ef2fa84d0533c3a577d"],
     * "artifacts":["675722b683828100658177c1"],
     * "teams":["6753782aebf3c97069ad69a4"]}
     */

    document.getElementById('projectName').innerText = "Name: "
        + project.projectName;
    document.getElementById(
        'projectDescription').innerText = "Description: "
        + project.projectDescription;
    document.getElementById(
        'projectDeadline').innerText = `Deadline: ${new Date(
        project.projectDeadline).toLocaleDateString()}`;
    document.getElementById(
        'projectStartingDate').innerText = `Creation date: ${new Date(
        project.projectCreationDate).toLocaleDateString()}`;

    // showing / hiding owner-only buttons
    if (project.owner.username === localStorage.getItem('username')) {
      console.log("Owner is the user");
      // if the project is owned by the user, show "you" next to the owner name
      document.getElementById('projectOwner').innerText = project.owner.username
          + " (you)";

      // the owner can delete the project
      deleteProjectButtonLogic();

      // showing and adding event listener to the new team button
      document.getElementById('newTeamButton').style.display = 'block';
      document.getElementById('newTeamButton').addEventListener('click', () => {
        // redirecting to create team page, passing the project id which is needed to link the team to the project
        window.location.href = `/team/${project.projectId}/create`;
      });

      // showing ad adding event listener to the new artifact button
      document.getElementById('newArtifactButton').style.display = 'block';
      document.getElementById('newArtifactButton').addEventListener('click',
          () => {
            // redirecting to create team page, passing the project id which is needed to link the team to the project
            window.location.href = `/artifact/${project.projectId}/create`;
          });

      startStopTaggingButtons(project.artifacts.map(artifact => artifact.artifactId));
    } else {
      // normal user stuff
      document.getElementById('projectOwner').innerText = project.owner.username + ` (mail: ${project.owner.email})`;
    }

    // adding user list
    const usersDropdown = document.querySelector(
        '#dropdownMenuButtonUsers + .dropdown-menu');
    usersDropdown.innerHTML = ''; // Clear existing items

    if (project.users.length === 0) {
      const noUser = {username: "No users Available"};
      showElementsInsideDropdown(usersDropdown, [noUser], false,
          '', '', '', defaultIcon, 'username');
    }

    showElementsInsideDropdown(usersDropdown, project.users, false, '', '', '',
        usersIcon, 'username');

    // adding team list
    const teamsDropdown = document.querySelector(
        '#dropdownMenuButtonTeams + .dropdown-menu');
    teamsDropdown.innerHTML = ''; // Clear existing items

    if (project.teams.length === 0) {
      const noTeam = {teamName: "No teams Available"};
      showElementsInsideDropdown(teamsDropdown, [noTeam], false,
          '', '', '', defaultIcon, 'teamName');
    }

    showElementsInsideDropdown(teamsDropdown, project.teams, true, '/team/',
        `/details/${project.owner.username}`, 'teamId', teamIcon, 'teamName');

    // adding team list
    const artifactsDropdown = document.querySelector(
        '#dropdownMenuButtonArtifacts + .dropdown-menu');
    artifactsDropdown.innerHTML = ''; // Clear existing items

    if (project.artifacts.length === 0) {
      const noArtifact = {artifactName: "No artifacts Available"};
      showElementsInsideDropdown(artifactsDropdown, [noArtifact], false,
          '', '', '', defaultIcon, 'artifactName');
    } else {
      console.log("Artifacts: " + JSON.stringify(project.artifacts));
      // TODO: mostra solo gli artefatti a cui l'utente ha accesso!
      showElementsInsideDropdown(artifactsDropdown,
          project.artifacts,
          true,
          '/artifact/',
          '/tag' + `/${project.owner.username}`,
          'artifactId',
          artifactsIcon,
          'artifactName');
    }

  })
  .catch(error => {
    console.error('Error:', error);
  });
});

function startStopTaggingButtons(artifactIds){
  const startTaggingButton = document.getElementById('startTaggingButton');
  const stopTaggingButton = document.getElementById('stopTaggingButton');

  startTaggingButton.style.display = 'block';
  stopTaggingButton.style.display = 'block';

  startTaggingButton.addEventListener('click', () => {
    console.log(artifactIds);
    startTagging(artifactIds);
  });

  stopTaggingButton.addEventListener('click', () => {
    stopTagging(artifactIds);
  });
}

function stopTagging(artifactIds){
  fetch(`/api/v1/artifact/stoptagging`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${localStorage.getItem('authToken')}`
    },
    body: JSON.stringify(artifactIds)
  })
  .then(response => response.json())
  .then(data => {
    alert(data.msg);
  })
  .catch(error => {
    console.error('Error:', error);
    alert('An error occurred while stopping tagging.');
  });
}

function startTagging(artifactIds){
  fetch(`/api/v1/artifact/starttagging`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${localStorage.getItem('authToken')}`
    },
    body: JSON.stringify(artifactIds)
  })
  .then(response => response.json())
  .then(data => {
    alert(data.msg);
  })
  .catch(error => {
    console.error('Error:', error);
    alert('An error occurred while stopping tagging.');
  });
}

function deleteProjectButtonLogic() {
  // get project id from url
  const urlParams = new URLSearchParams(window.location.search);
  const projectId = urlParams.get('id');

  const deleteProjectButton = document.getElementById('deleteProjectButton');
  deleteProjectButton.style.display = 'block';
  deleteProjectButton.addEventListener('click', () => {
    if (projectId === "" || projectId === null || projectId === undefined) {
      alert("Error: team ID not found");
      return;
    }

    const confirmation = confirm(
        'Are you sure you want to delete this project? This action is ' +
        'irreversible and will also delete all the teams and artifacts ' +
        'associated with it.');
    if (!confirmation) {
      return;
    }

    // deleting the team
    fetch("/api/v1/project/" + projectId, {
      method: 'DELETE', headers: {
        'Authorization': 'Bearer ' + localStorage.getItem('authToken'),
      }
    })
    .then(response => {
      if (response.ok) {
        return response.json().then(data => {

          // updating local cache
          const projectIds = JSON.parse(localStorage.getItem('user-projectIds'))
              || [];
          const updatedProjectIds = projectIds.filter(id => id !== projectId);
          localStorage.setItem('user-projectIds',
              JSON.stringify(updatedProjectIds));

          alert(data.msg);
          const redirectUrl = "/project/"
              + localStorage.getItem("username")
              + "/projects";
          console.log("redirect: " + redirectUrl);
          window.location.href = redirectUrl;
        });
      } else {
        return response.json().then(body => {
          console.log("response: " + body.msg);
          alert("Error updating emails: " + body.msg);
        });
      }
    })
    .catch(error => console.error('Error fetching user projects:', error));
  });
}

/**
 * Show elements inside a dropdown.
 *
 * @param targetDropdown the dropdown where the elements will be shown
 * @param list the list of elements to show
 * @param clickable if the elements are clickable
 * @param headerUrl the url to append before the id
 * @param footerUrl the url to append after the id
 * @param itemIdName the name of the id attribute
 * @param icon the icon to show before the text
 * @param attributeName the name of the attribute to show inside the dropdown
 */
function showElementsInsideDropdown(targetDropdown,
    list,
    clickable = false,
    headerUrl,
    footerUrl,
    itemIdName,
    icon = defaultIcon,
    attributeName,
) {
  list.forEach(listItem => {
    // Creating an element <li> for each team
    const wrapperItem = document.createElement('li');

    // Creating the <a> element
    const link = document.createElement('a');
    if (clickable) {
      link.href = headerUrl + listItem[itemIdName] + footerUrl;
    }

    link.style.textDecoration = 'none';
    link.style.color = 'inherit'; // To inherit the color from the parent

    // Creating SVG icon
    const iconElement = document.createElement('span');
    iconElement.innerHTML = icon;
    iconElement.style.marginLeft = '1em';
    iconElement.style.marginRight = '1em';

    // Adding SVG icon to <a>
    link.appendChild(iconElement);

    // Adding team
    const textToShow = document.createElement('span');
    textToShow.textContent = listItem[attributeName];
    textToShow.style.marginLeft = '0.3em';

    // Adding artifactName to <a>
    link.appendChild(textToShow);

    // Adding <a> to <li>
    wrapperItem.appendChild(link);

    // Adding <li> to dropdown
    targetDropdown.appendChild(wrapperItem);
  });
}