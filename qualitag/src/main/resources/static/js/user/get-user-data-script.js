document.addEventListener('DOMContentLoaded', function () {
  const authToken = localStorage.getItem('authToken');

  // Extract username from URL
  const urlParts = window.location.pathname.split('/');
  const username = urlParts[2]; // Assuming the path is /user/{username} or /user/{username}/update

  if (username) {
    fetch('/api/v1/user/' + username, {
      headers: {
        'Authorization': 'Bearer ' + authToken
      }
    })
    .then(response => {
      if (response.status === 403 || response.status === 404) {
        window.location.href = '/404'; // Redirect to 404 page
        throw new Error('Unauthorized access or user not found');
      }
      return response.json();
    })
    .then(user => {
      console.log('Fetched user data:', user); // Log the user object
      if (user) {
        // Update elements dynamically
        setElementValue('username', user.username);
        setElementValue('email', user.email);
        setElementValue('name', user.name);
        setElementValue('surname', user.surname);
      } else {
        alert('User data is null or undefined');
      }
    })
    .catch(error => console.error('Error fetching user data:', error));
  } else {
    alert('No user logged in');
    window.location.href = '/signin';
  }
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
