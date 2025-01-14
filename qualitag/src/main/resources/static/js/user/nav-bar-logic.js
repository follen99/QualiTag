/**
 * USED component IDs:
 * - DOMContentLoaded
 * - logoutbutton
 * - userProjects
 * - profileLink
 */
import { refreshUserData } from './refresh-user-data.js';

// If user is logged in, show the profile link and logout button
document.addEventListener('DOMContentLoaded', async function () {
    const profileLink = document.getElementById('profileLink');
    const logoutButton = document.getElementById('logoutbutton');
    const loginButton = document.getElementById('login-navbar');
    const registerButton = document.getElementById('register-navbar');
    const projectsButton = document.getElementById('projects-navbar');
    const refreshUserDataButton = document.getElementById('refreshUserDataButton');
    const dropdownMenu = document.getElementById('dropdown-menu');

    const profileBigButton = document.getElementById('profile-big-button');
    const logoutBigButton = document.getElementById('logout-big-button');

    // if user-data is older than 1 hour, redirect to login page
    if (localStorage.getItem('user-lastFetch') && (Date.now() - localStorage.getItem('user-lastFetch') > 3600000)) {
        alert('Your session has expired. Please log in again.');
        localStorage.clear();
        window.location.href = '/signin';
        return;
    }

    await checkToken();
    const authToken = localStorage.getItem('authToken');

    if (!authToken) {
        // user NOT logged in
        if (profileLink) profileLink.style.display = 'none';     // profile link
        if (logoutButton) logoutButton.style.display = 'none';    // logout button
        if (projectsButton) projectsButton.style.display = 'none';  // my projects button
        if (refreshUserDataButton) refreshUserDataButton.style.display = 'none'; // refresh user data button

        if (loginButton) loginButton.style.display = 'block'; // Mostra il bottone
        if (registerButton) registerButton.style.display = 'block'; // Mostra il bottone
    }else {
        // user logged in
        if (profileLink) profileLink.style.display = 'block'; // Mostra il bottone
        if (logoutButton) logoutButton.style.display = 'block'; // Mostra il bottone
        if (projectsButton) projectsButton.style.display = 'block'; // Mostra il bottone
        if (refreshUserDataButton) refreshUserDataButton.style.display = 'block'; // Mostra il bottone

        if (loginButton) loginButton.style.display = 'none'; // Nascondi il bottone
        if (registerButton) registerButton.style.display = 'none'; // Nascondi il bottone
    }

    // Hide dropdown menu if on the home page with path "/"
    if (window.location.pathname === '/') {
        if (dropdownMenu) {
            dropdownMenu.style.display = 'none';
        }

        if (!authToken) {
            // not logged --> hide profile and logout buttons
            if (profileBigButton) profileBigButton.style.display = 'none';
            if (logoutBigButton) logoutBigButton.style.display = 'none';
        }else {
            // logged --> show profile and logout buttons
            if (profileBigButton) profileBigButton.style.display = 'block';
            if (logoutBigButton) logoutBigButton.style.display = 'block';
        }
    }


    // Profile redirect
    if (profileLink) profileLink.addEventListener('click', profileRedirectHandler);
    if (profileBigButton) profileBigButton.addEventListener('click', profileRedirectHandler);

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
    if (logoutButton) logoutButton.addEventListener('click', logoutHandler);
    if (logoutBigButton) logoutBigButton.addEventListener('click', logoutHandler);


    // refresh user data
    if (refreshUserDataButton) refreshUserDataButton.addEventListener('click', refreshHandler);

        
});
function profileRedirectHandler(event) {
    const username = localStorage.getItem('username');
    if (username) {
        this.setAttribute('href', '/user/' + username);
    } else {
        event.preventDefault(); // Prevent the default behavior
        alert('Please login to access the profile page');
        window.location.href = '/signin';
    }
}

function logoutHandler(event) {
    event.preventDefault(); // Prevent the default behavior
    if (confirm('Are you sure you want to log out?')) {
        alert('User ' + localStorage.getItem('username') + ' has been logged out successfully.\n' + 'You can now register or login again.');
        localStorage.removeItem('username');
        localStorage.removeItem('authToken');
        location.reload(); // Reload the current page
    }
}

function refreshHandler(event) {
    event.preventDefault(); // Prevent the default behavior
    console.log('Refreshing user data...');
    refreshUserData();
}

async function checkToken() {
    const authToken = localStorage.getItem('authToken');
    if (!authToken) {
        return false;
    }

    const response = await fetch('/api/v1/auth/check-token', {
        method: 'GET', headers: {
            'Content-Type': 'application/json', 'Authorization': 'Bearer ' + authToken
        }
    });

    if (!response.ok) {
        // alert('Your session has expired. Please log in again.');
        localStorage.clear(); // Clear all items in local storage
        return false;
        // window.location.href = '/signin'; // Redirect to login page
    }

    console.log(response);
    return true;
}




