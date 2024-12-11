document.getElementById("forgotPasswordForm").addEventListener("submit", async function (event) {
  event.preventDefault(); // Prevent the default form submission

  const email = document.getElementById("email").value;

  // Validate email
  if (!email) {
    alert("Please enter a valid email address.");
    return;
  }

  // Send a POST request to the server
  try {
    const response = await fetch("/api/v1/auth/forgot-password", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({email: email}),
    });

    const responseData = await response.json();

    if (response.ok) {
      alert(responseData.msg || "Password reset email sent successfully.");
      window.location.href = "/"; // Redirect to the home page
    } else {
      alert("Error sending email. Please try again.");
    }
  } catch (error) {
    alert('An error occurred: ' + error.message);
  }
});
