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
    const dropdownButton = document.getElementById('dropdown-button');

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
        profileLink.style.display = 'none';     // profile link
        logoutButton.style.display = 'none';    // logout button
        projectsButton.style.display = 'none';  // my projects button
        dropdownButton.style.display = 'none';  // dropdown button


        loginButton.style.display = 'block'; // Mostra il bottone
        registerButton.style.display = 'block'; // Mostra il bottone
    }else {
        // user logged in
        profileLink.style.display = 'block'; // Mostra il bottone
        logoutButton.style.display = 'block'; // Mostra il bottone
        projectsButton.style.display = 'block'; // Mostra il bottone
        dropdownButton.style.display = 'block';  // dropdown button

        loginButton.style.display = 'none'; // Nascondi il bottone
        registerButton.style.display = 'none'; // Nascondi il bottone
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
            alert('User ' + localStorage.getItem('username') + ' has been logged out successfully.\n' + 'You can now register or login again.');
            localStorage.removeItem('username');
            localStorage.removeItem('authToken');
            location.reload(); // Reload the current page
        }
    });

    // refresh user data
    document.getElementById('refreshUserDataButton').addEventListener('click', async function (event) {
        event.preventDefault(); // Prevent the default behavior
        console.log('Refreshing user data...');
        await refreshUserData();
    });
        
});


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




