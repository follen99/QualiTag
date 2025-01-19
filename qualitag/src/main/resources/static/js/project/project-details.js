let globalProjectData;

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
    method: 'GET', headers: {
      'Authorization': `Bearer ${token}`
    }
  })
  .then(response => {
    if (!response.ok) {
      return response.json().then(errorData => {
        // get error message
        console.log("Error message: " + errorData.msg);
        alert(errorData.msg);
      });
    }
    return response.json(); // Se la risposta è ok, continua con il parsing dei dati
  })
  .then(project => {
    globalProjectData = project;
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

    document.getElementById('projectName').innerText = project.projectName;
    document.getElementById(
        'projectDescription').innerText = project.projectDescription;
    document.getElementById(
        'projectDeadline').innerText = `Deadline: ${new Date(
        project.projectDeadline).toLocaleDateString()}`;
    document.getElementById(
        'projectStartingDate').innerText = `Deadline: ${new Date(
        project.projectCreationDate).toLocaleDateString()}`;

    // TODO add a card to display more infos about the owner
    document.getElementById('projectOwner').innerText = project.owner.username;

    // adding user list
    const usersDropdown = document.querySelector(
        '#dropdownMenuButtonUsers + .dropdown-menu');
    usersDropdown.innerHTML = ''; // Clear existing items

    project.users.forEach(user => {
      // Creating an element <li> for each user
      const userItem = document.createElement('li');

      // Creating SVG icon
      const icon = document.createElement('span');
      icon.innerHTML = `
                        <svg xmlns="http://www.w3.org/2000/svg" width="1em" height="1em" fill="currentColor" class="bi bi-person" viewBox="0 0 16 16">
                            <path d="M8 8a3 3 0 1 0 0-6 3 3 0 0 0 0 6m2-3a2 2 0 1 1-4 0 2 2 0 0 1 4 0m4 8c0 1-1 1-1 1H3s-1 0-1-1 1-4 6-4 6 3 6 4m-1-.004c-.001-.246-.154-.986-.832-1.664C11.516 10.68 10.289 10 8 10s-3.516.68-4.168 1.332c-.678.678-.83 1.418-.832 1.664z"/>
                        </svg>
                    `;
      icon.style.marginLeft = '1em';

      // Adding SVG icon to <li>
      userItem.appendChild(icon);

      // Adding username
      const username = document.createElement('span');
      username.textContent = user.username;
      username.style.marginLeft = '0.3em';

      // adding username to <li>
      userItem.appendChild(username);

      // adding <li> to dropdown
      usersDropdown.appendChild(userItem);
    });

    // adding team list
    const teamsDropdown = document.querySelector(
        '#dropdownMenuButtonTeams + .dropdown-menu');
    teamsDropdown.innerHTML = ''; // Clear existing items

    project.teams.forEach(team => {
      // Creating an element <li> for each team
      const teamItem = document.createElement('li');

      // Creating SVG icon
      const icon = document.createElement('span');
      icon.innerHTML = `
                        <svg xmlns="http://www.w3.org/2000/svg" width="1em" height="1em" fill="currentColor" class="bi bi-microsoft-teams" viewBox="0 0 16 16">
                          <path d="M9.186 4.797a2.42 2.42 0 1 0-2.86-2.448h1.178c.929 0 1.682.753 1.682 1.682zm-4.295 7.738h2.613c.929 0 1.682-.753 1.682-1.682V5.58h2.783a.7.7 0 0 1 .682.716v4.294a4.197 4.197 0 0 1-4.093 4.293c-1.618-.04-3-.99-3.667-2.35Zm10.737-9.372a1.674 1.674 0 1 1-3.349 0 1.674 1.674 0 0 1 3.349 0m-2.238 9.488-.12-.002a5.2 5.2 0 0 0 .381-2.07V6.306a1.7 1.7 0 0 0-.15-.725h1.792c.39 0 .707.317.707.707v3.765a2.6 2.6 0 0 1-2.598 2.598z"/>
                          <path d="M.682 3.349h6.822c.377 0 .682.305.682.682v6.822a.68.68 0 0 1-.682.682H.682A.68.68 0 0 1 0 10.853V4.03c0-.377.305-.682.682-.682Zm5.206 2.596v-.72h-3.59v.72h1.357V9.66h.87V5.945z"/>
                        </svg>
                    `;
      icon.style.marginLeft = '1em';

      // Adding SVG icon to <li>
      teamItem.appendChild(icon);

      // Adding team
      const teamName = document.createElement('span');
      teamName.textContent = team.teamName;
      teamName.style.marginLeft = '0.3em';

      // adding teamName to <li>
      teamItem.appendChild(teamName);

      // adding <li> to dropdown
      teamsDropdown.appendChild(teamItem);

      // adding click event listener on each team
      /*teamItem.addEventListener('click', () => {
        alert('fetching from: ' + `/team/${team.teamId}/details`);
        fetch(`/team/${team.teamId}/details`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
          },
          body: JSON.stringify(team)
        })
        .then(response => response.text())
        .then(html => {
          document.open();
          document.write(html);
          document.close();
        })
        .catch(error => console.error('Error:', error));
      });*/

      teamItem.addEventListener('click', () => {
        window.location.href = `/team/${team.teamId}/details`;
      });
    });

    // adding team list
    const artifactsDropdown = document.querySelector(
        '#dropdownMenuButtonArtifacts + .dropdown-menu');
    artifactsDropdown.innerHTML = ''; // Clear existing items

    project.artifacts.forEach(artifact => {
      // Creating an element <li> for each team
      const teamItem = document.createElement('li');

      // Creating SVG icon
      const icon = document.createElement('span');
      icon.innerHTML = `
                        <svg xmlns="http://www.w3.org/2000/svg" width="1em" height="1em" fill="currentColor" class="bi bi-card-text" viewBox="0 0 16 16">
                          <path d="M14.5 3a.5.5 0 0 1 .5.5v9a.5.5 0 0 1-.5.5h-13a.5.5 0 0 1-.5-.5v-9a.5.5 0 0 1 .5-.5zm-13-1A1.5 1.5 0 0 0 0 3.5v9A1.5 1.5 0 0 0 1.5 14h13a1.5 1.5 0 0 0 1.5-1.5v-9A1.5 1.5 0 0 0 14.5 2z"/>
                          <path d="M3 5.5a.5.5 0 0 1 .5-.5h9a.5.5 0 0 1 0 1h-9a.5.5 0 0 1-.5-.5M3 8a.5.5 0 0 1 .5-.5h9a.5.5 0 0 1 0 1h-9A.5.5 0 0 1 3 8m0 2.5a.5.5 0 0 1 .5-.5h6a.5.5 0 0 1 0 1h-6a.5.5 0 0 1-.5-.5"/>
                        </svg>
                    `;
      icon.style.marginLeft = '1em';

      // Adding SVG icon to <li>
      teamItem.appendChild(icon);

      // Adding team
      const artifactName = document.createElement('span');
      artifactName.textContent = artifact.artifactName;
      artifactName.style.marginLeft = '0.3em';

      // adding artifactName to <li>
      teamItem.appendChild(artifactName);

      // adding <li> to dropdown
      artifactsDropdown.appendChild(teamItem);
    });

  })
  .catch(error => {
    console.error('Error:', error);
  });

  document.getElementById('newTeamButton').addEventListener('click', () => {
    // redirecting to create team page, passing the project id which is needed to link the team to the project
    window.location.href = `/team/${globalProjectData.projectId}/create`;
  });

});