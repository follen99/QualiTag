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

    if (!localStorage.getItem('user-projectIds') || localStorage.getItem('user-projectIds') === '[]') {
        thereAreNoProjects();
        return;
    }


    const requestBody = localStorage.getItem('user-projectIds'); //JSON.stringify(localStorage.getItem('user-projectIds'));
    console.log('requestBody:', requestBody);

    const response = fetch('/api/v1/projects/get-by-ids', {
        method: 'POST',
        headers: {
            'Authorization': 'Bearer ' + authToken,
            'Content-Type': 'application/json'
        },
        body: requestBody
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
        console.log('project:', project);
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

        projectListContainer.appendChild(projectCard);
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