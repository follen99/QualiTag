document.addEventListener('DOMContentLoaded', async function () {
  const authToken = localStorage.getItem('authToken');
  const username = localStorage.getItem('username');

  console.log('authToken:', authToken);

  console.log('sending: ' + localStorage.getItem('user-projectIds'));

  if (!authToken || !username) {
    alert('Please login to access your projects');
    window.location.href = '/signin';
    return;
  }

  if (!localStorage.getItem('user-projectIds') || localStorage.getItem(
      'user-projectIds') === '[]') {
    thereAreNoProjects();
    return;
  }

  const requestBody = localStorage.getItem('user-projectIds'); //JSON.stringify(localStorage.getItem('user-projectIds'));
  console.log('requestBody:', requestBody);

  const response = fetch('/api/v1/project/get-by-ids', {
    method: 'POST', headers: {
      'Authorization': 'Bearer ' + authToken, 'Content-Type': 'application/json'
    }, body: requestBody
  })
  .then(response => response.json())
  .then(userProjects => {
    if (userProjects.length === 0) {
      thereAreNoProjects();
    } else {
      displayProjects(userProjects);
    }
  })
  .catch(error => console.error('Error fetching user projects:', error));

});

function displayProjects(projects) {
  const projectListContainer = document.getElementById('projectsList');
  projects.forEach(project => {
    // encapsulate project data in a button so that it can be clicked
    // make the button look like a card
    const projectButton = document.createElement('button');
    projectButton.className = 'project-button';
    projectButton.style.border = 'none';
    projectButton.style.background = 'none';
    projectButton.style.padding = '0';
    projectButton.style.margin = '0';
    projectButton.style.width = '100%';
    projectButton.style.textAlign = 'left';
    projectButton.style.cursor = 'pointer';
    projectButton.style.color = 'white';

    const projectCard = document.createElement('div');
    projectCard.className = 'project-card';

    const projectTitle = document.createElement('div');
    projectTitle.className = 'project-title';
    projectTitle.textContent = project.projectName;

    const projectDescription = document.createElement('div');
    projectDescription.className = 'project-description';
    projectDescription.textContent = project.projectDescription;

    const projectStatus = document.createElement('div');
    projectStatus.className = 'project-status';
    projectStatus.textContent = `Status: ${project.projectStatus}`;

    projectCard.appendChild(projectTitle);
    projectCard.appendChild(projectDescription);
    projectCard.appendChild(projectStatus);

    projectButton.appendChild(projectCard);
    projectListContainer.appendChild(projectButton);

// Add click event listener
    projectButton.addEventListener('click', () => {
      window.location.href = `/project/detail?id=${project.projectId}`;
    });
  });
}


function thereAreNoProjects() {
  const projectListContainer = document.getElementById('projectsList');
  const listItem = document.createElement('li');
  listItem.textContent = 'There are no projects to display.';
  projectListContainer.appendChild(listItem);

  // Create "Go Home" link
  const goHomeLink = document.createElement('a');
  goHomeLink.href = '/';
  goHomeLink.textContent = 'Go Home';
  goHomeLink.style.paddingRight = '10px';
  projectListContainer.appendChild(goHomeLink);

  // Create "Create New Project" link
  const createProjectLink = document.createElement('a');
  createProjectLink.href = '/project/create';
  createProjectLink.textContent = 'Create New Project';
  projectListContainer.appendChild(createProjectLink);
}