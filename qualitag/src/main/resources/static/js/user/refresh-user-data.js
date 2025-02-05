/**
 * Refreshes the user data stored in the local storage.
 */
export async function refreshUserData() {
    try {
      const response = await fetch(`/api/v1/user/${localStorage.getItem('username')}`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('authToken')}`
        }
      });
  
      const responseData = await response.json();
  
      if (response.ok) {
        const user = responseData;
        refreshLocally(user.username, user.email, user.projectRoles, user.name, user.surname, user.projectIds, user.teamIds, user.userId);

      } else {
        console.error('Failed to refresh user data:', responseData.message);
      }
    } catch (error) {
      console.error('Error refreshing user data:', error);
    }
    window.location.reload();
  }

  export async function refreshLocally(username, email, projectRoles, name, surname, projectIds, teamIds, userId) {
    localStorage.setItem('username', username);
    localStorage.setItem('user-email', email);
    localStorage.setItem('user-projectRoles', JSON.stringify(projectRoles));
    localStorage.setItem('user-name', name);
    localStorage.setItem('user-surname', surname);
    localStorage.setItem('user-projectIds', JSON.stringify(projectIds));
    localStorage.setItem('user-tagIds', JSON.stringify(teamIds));
    localStorage.setItem('user-lastFetch', Date.now());
    localStorage.setItem('user-userId', JSON.stringify(userId));
  }
