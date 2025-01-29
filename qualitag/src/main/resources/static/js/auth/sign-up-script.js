import {LoadingModal} from "../modals/loading-modal.js";

document.getElementById('registrationForm').addEventListener('submit',
    async function (event) {
      event.preventDefault();

      const loadingModal = new LoadingModal("/", "Registering user...");

      const formData = new FormData(event.target);
      const data = Object.fromEntries(formData.entries());

      loadingModal.showModal(null, true, false, false);
      try {
        // TODO: if mail system does not work, tell to user
        const response = await fetch('/api/v1/auth/register', {
          method: 'POST', headers: {
            'Content-Type': 'application/json'
          }, body: JSON.stringify(data)
        });

        const responseData = await response.json(); // Parse as JSON

        if (response.ok) {
          // Success: Use the token from the JSON response
          localStorage.setItem('authToken', responseData.token);
          localStorage.setItem('username', responseData.username);
          // alert(responseData.msg || 'Registration successful');
          // window.location.href = '/'; // Redirect to homepage after registration

          loadingModal.showModal("Registration successful!\n Check your emails.", false, true, true);
        } else {
          // Error: Display the error message from the JSON response
          loadingModal.showModal(
              "Something went wrong!\n Message: " + responseData.msg
              , false, true, false);
        }
      } catch (error) {
        alert('An error occurred: ' + error.message);
      }
    });