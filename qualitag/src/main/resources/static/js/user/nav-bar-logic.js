/**
 * USED component IDs:
 * - DOMContentLoaded
 * - logoutbutton
 * - userProjects
 * - profileLink
 */

// If user is logged in, show the profile link and logout button
document.addEventListener('DOMContentLoaded', function() {
    const profileLink = document.getElementById('profileLink');
    const logoutButton = document.getElementById('logoutbutton');
    const loginButton = document.getElementById('login-navbar');
    const registerButton = document.getElementById('register-navbar');
    const projectsButton = document.getElementById('projects-navbar');

    const username = localStorage.getItem('username');

    if (username) {
        profileLink.style.display = 'block'; // Mostra il bottone
        logoutButton.style.display = 'block'; // Mostra il bottone
        projectsButton.style.display = 'block'; // Mostra il bottone

        loginButton.style.display = 'none'; // Nascondi il bottone
        registerButton.style.display = 'none'; // Nascondi il bottone

    } else {
        profileLink.style.display = 'none'; // Nascondi il bottone
        logoutButton.style.display = 'none'; // Nascondi il bottone
        projectsButton.style.display = 'none'; // Nascondi il bottone

        loginButton.style.display = 'block'; // Mostra il bottone
        registerButton.style.display = 'block'; // Mostra il bottone
    }

    // Profile redirect
    document.getElementById('profileLink').addEventListener('click', function (event) {
        const username = localStorage.getItem('username');
        if (username) {
            this.setAttribute('href', '/user/' + username);
        } else {
            event.preventDefault(); // Prevent the default behavior
            alert('Please login to access the profile page');
            window.location.href = '/signin';
        }
    });

    // projects redirect
    document.getElementById('projects-navbar').addEventListener('click', function (event) {
        const username = localStorage.getItem('username');
        if (username) {
            this.setAttribute('href', '/project/' + username + '/projects');
        } else {
            event.preventDefault(); // Prevent the default behavior
            alert('Please login to access the profile page');
            window.location.href = '/signin';
        }
    });

    // logout button logic
    document.getElementById('logoutbutton').addEventListener('click', function (event) {
        event.preventDefault(); // Prevent the default behavior
        if (confirm('Are you sure you want to log out?')) {
            alert('User ' + localStorage.getItem('username') + ' has been logged out successfully.\n' +
                'You can now register or login again.');
            localStorage.removeItem('username');
            localStorage.removeItem('authToken');
            location.reload(); // Reload the current page
        }
    });
});





