/*
* This script fetches user data from the localStorage object.
* TODO: refresh data from the server after a certain period of time.
* */

document.addEventListener('DOMContentLoaded', function () {
    const authToken = localStorage.getItem('authToken');

    // if we don't handle this case, an exception will be thrown
    if (!authToken) {
        alert('You must be logged in to access this page.');
        localStorage.clear();
        window.location.href = '/signin';
        return;
    }

    const lastFetch = localStorage.getItem('user-lastFetch');
    if (!lastFetch || (Date.now() - lastFetch > 3600000)) { // 3600000 ms = 1 hour
        alert('Your session has expired. Please log in again.');
        localStorage.clear();
        window.location.href = '/signin';
        return;
    }

    setElementValue('username', localStorage.getItem('username'));
    setElementValue('email', localStorage.getItem('user-email'));
    setElementValue('name', localStorage.getItem('user-name'));
    setElementValue('surname', localStorage.getItem('user-surname'));
});

/**
 * Dynamically updates the value or text content of an element
 * based on its type (form input or static element).
 *
 * @param {string} elementId - The ID of the element to update.
 * @param {string} value - The value to set for the element.
 */
function setElementValue(elementId, value) {
    const element = document.getElementById(elementId);
    if (element) {
        if (element.tagName === 'INPUT' || element.tagName === 'TEXTAREA') {
            // For form elements like <input> or <textarea>
            element.value = value || '';
        } else {
            // For other elements like <span> or <p>
            element.textContent = value || '';
        }
    }
}
