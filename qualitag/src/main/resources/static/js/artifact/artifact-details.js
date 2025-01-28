const defaultIcon = '<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-app" viewBox="0 0 16 16">\n'
    + '  <path d="M11 2a3 3 0 0 1 3 3v6a3 3 0 0 1-3 3H5a3 3 0 0 1-3-3V5a3 3 0 0 1 3-3zM5 1a4 4 0 0 0-4 4v6a4 4 0 0 0 4 4h6a4 4 0 0 0 4-4V5a4 4 0 0 0-4-4z"/>\n'
    + '</svg>';

const tagIcon = '<svg xmlns="http://www.w3.org/2000/svg" width="1em" height="1em" fill="currentColor" class="bi bi-tag" viewBox="0 0 16 16">\n'
    + '  <path d="M6 4.5a1.5 1.5 0 1 1-3 0 1.5 1.5 0 0 1 3 0m-1 0a.5.5 0 1 0-1 0 .5.5 0 0 0 1 0"/>\n'
    + '  <path d="M2 1h4.586a1 1 0 0 1 .707.293l7 7a1 1 0 0 1 0 1.414l-4.586 4.586a1 1 0 0 1-1.414 0l-7-7A1 1 0 0 1 1 6.586V2a1 1 0 0 1 1-1m0 5.586 7 7L13.586 9l-7-7H2z"/>\n'
    + '</svg>';

// Main control flow
let allTagsContainer;
document.addEventListener('DOMContentLoaded', async function () {
  const artifactContainer = document.getElementById('artifact-container');
  allTagsContainer = document.getElementById('tags-container');

  const tagInput = document.getElementById('tagInput');

  const artifactId = window.location.pathname.split('/')[2];

  const ownerUsername = document.getElementById(
      'ownerUsernameContainer').textContent.trim();
  console.log("ownerUsername: " + ownerUsername);

  // before displaying the artifact, check artifact metadata
  const metadata = await fetchArtifactMetadata(artifactId);
  console.log("metadata:", metadata);

  // fetch and display artifact content
  fetchArtifact(artifactId);

  // if the user is the owner of the artifact he cannot tag it,
  // but he can stop the tagging operation, see other users' tags and IRR
  if (localStorage.getItem('username') === ownerUsername) {
    populateSidebarOwner(artifactId, metadata);
  } else {
    // if the user is not the owner, he can tag the artifact
    populateSidebarUser(artifactId, metadata);
  }

});

async function fetchArtifactMetadata(artifactId) {
  return fetch(`/api/v1/artifact/${artifactId}/metadata`, {
    method: 'GET',
    headers: {
      'Authorization': `Bearer ${localStorage.getItem('authToken')}`
    }
  }).then(response => {
    if (response.ok) {
      return response.json();
    } else {
      return response.json().then(errorData => {
        console.error("Error message: " + errorData.msg);
        alert("Error: " + errorData.msg);
        throw new Error(errorData.msg);
      });
    }
  });
}

function fetchAllArtifactTags(artifactId) {
  return fetch(`/api/v1/artifact/${artifactId}/tags`, {
    method: 'GET',
    headers: {
      'Authorization': `Bearer ${localStorage.getItem('authToken')}`
    }
  }).then(response => {
    if (response.ok) {
      return response.json();
    } else {
      return response.json().then(errorData => {
        console.error("Error message: " + errorData.msg);
        alert("Error: " + errorData.msg);
        throw new Error(errorData.msg);
      });
    }
  });
}

/**
 * Fetches the tags for the artifact with the given ID/username/email.
 *
 * @param artifactId  the ID of the artifact to fetch tags for
 * @param username the username of the user to fetch tags for
 */
function fetchTagsFromUser(artifactId, username) {
  return fetch(`/api/v1/artifact/${artifactId}/${username}/tags`, {
    method: 'GET',
    headers: {
      'Authorization': `Bearer ${localStorage.getItem('authToken')}`
    }
  }).then(response => {
    if (response.ok) {
      return response.json();
    } else {
      return response.json().then(errorData => {
        console.error("Error message: " + errorData.msg);
        alert("Error: " + errorData.msg);
        throw new Error(errorData.msg);
      });
    }
  });
}

function deleteTagById(tagId) {
  return fetch(`/api/v1/tag/${tagId}`, {
    method: 'DELETE',
    headers: {
      'Authorization': `Bearer ${localStorage.getItem('authToken')}`
    }
  }).then(response => {
    if (response.ok) {
      return response.json();
    } else {
      return response.json().then(errorData => {
        console.error("Error message: " + errorData.msg);
        alert("Error: " + errorData.msg);
        throw new Error(errorData.msg);
      });
    }
  });
}

/**
 * Saves the tags for the artifact with the given ID.
 *
 * @param artifactId  the ID of the artifact to save tags for
 * @param userId  the userId of the user saving the tags
 * @param hex the hex color code for the tags
 * @param tags the tags to save (array of strings)
 */
function saveTags(artifactId, userId, hex, tags) {
  const tagCreateDtoList = tags.map(tagValue => ({
    tagValue: tagValue,
    createdBy: userId,
    colorHex: hex
  }));

  fetch(`/api/v1/tag/${artifactId}/addtags`, {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${localStorage.getItem('authToken')}`,
      'Content-Type': 'application/json'
    }, body: JSON.stringify(tagCreateDtoList)
  }).then(response => {
    if (response.ok) {
      return response.json().then(data => {
        alert("Response: " + data.msg);
        console.log("data: " + JSON.stringify(data));
        window.location.reload();
      });
    } else {
      return response.json().then(errorData => {
        console.error("Error message: " + errorData.msg);
        alert("Error: " + errorData.msg);
      });
    }
  });
}

/**
 * Fetches the artifact with the given ID and displays it in the given container.
 *
 * @param artifactId  the ID of the artifact to fetch
 * @returns {Promise<void>} a promise that resolves when the artifact is fetched and displayed
 */
async function fetchArtifact(artifactId) {
  const artifactContainer = document.getElementById('artifact-container');

  try {
    const response = await fetch(`/api/v1/artifact/${artifactId}`, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('authToken')}`
      }
    });

    if (!response.ok) {
      const errorResponse = await response.json();
      alert(`Error: ${errorResponse.msg}`);
      throw new Error(errorResponse.msg || 'Failed to fetch artifact');
    }

    // gettin headers content
    const headers = response.headers;
    const contentType = headers.get('content-type');
    const contentDisposition = headers.get('content-disposition');
    const filename = contentDisposition
        ? contentDisposition.split('filename=')[1].replace(/"/g, '')
        : 'downloaded_file';

    // get the file as a blob
    const fileBlob = await response.blob();

    // visualize the file
    displayArtifact(fileBlob, contentType, artifactContainer);
  } catch (error) {
    artifactContainer.innerHTML = `<p>${error.message}</p>`;
  }
}

// determine file type
function detectLanguage(contentType) {
  if (contentType.includes('javascript')) {
    return 'language-javascript';
  }
  if (contentType.includes('java')) {
    return 'language-java';
  }
  if (contentType.includes('python')) {
    return 'language-python';
  }
  if (contentType.includes('html')) {
    return 'language-html';
  }
  if (contentType.includes('css')) {
    return 'language-css';
  }
  return 'plaintext'; // Default
}

async function populateSidebarOwner(artifactId, metadata) {
  // TODO: mostrare tutti i tag aggiunti dagli altri utenti finora, mostrare IRR

  const sidebarContainer = document.getElementById('sidebar-container');
  sidebarContainer.innerHTML = ''; // Clear existing content

  const message = document.createElement('p');
  message.textContent = 'You are the owner, you cannot tag an artifact; you can only stop the tagging operation.';
  sidebarContainer.appendChild(message);


  console.log("metadata:", metadata.artifact.isTaggingOpen);
  if (metadata.artifact.isTaggingOpen) {
    sidebarContainer.appendChild(getExplainingText('The tagging operation is currently in progress. You can stop it by clicking the button below.'));

    const stopButton = document.createElement('button');
    stopButton.textContent = 'Stop Tagging';
    stopButton.style.marginBottom = '2em';
    stopButton.className = 'btn btn-danger mt-3';
    stopButton.addEventListener('click', async () => {
      if (confirm(
          "Are you sure you want to stop the tagging operation?\n This will prevent other users from adding tags.")) {
        alert(await stopTaggingOperation(artifactId));

        window.location.reload();
      }
    });
    sidebarContainer.appendChild(stopButton);
  } else {

    sidebarContainer.appendChild(getExplainingText('No user can tag the artifact since the tagging operation is closed. You can resume it by clicking the button below.'));

    const startButton = document.createElement('button');
    startButton.textContent = 'Start Tagging';
    startButton.style.marginBottom = '2em';
    startButton.className = 'btn btn-primary mt-3';
    startButton.addEventListener('click', async () => {
      if (confirm(
          "Are you sure you want to start the tagging operation?\n This will allow other users to add tags.")) {
        alert(await startTaggingOperation(artifactId));

        window.location.reload();
      }
    });
    sidebarContainer.appendChild(startButton);
  }

  const response = await fetchAllArtifactTags(artifactId);
  const tags = response.tags; // Array of tags

  console.log("tags:", tags);

  if (Array.isArray(tags) && tags.length > 0) {
    const explanatoryText = document.createElement('p');
    explanatoryText.textContent = 'Tags from all the users:';
    sidebarContainer.appendChild(explanatoryText);

    for (const tag of tags) {
      const tagContainer = document.createElement('div'); // Creazione dinamica del contenitore per ogni tag
      tagContainer.className = 'd-flex justify-content-between align-items-center border p-2 mb-2 rounded';
      tagContainer.style.backgroundColor = '#334E82'; // Sfondo leggero per separare i tag

      // Sezione per visualizzare il colore
      const colorIndicator = document.createElement('div');
      colorIndicator.style.width = '20px';
      colorIndicator.style.height = '20px';
      colorIndicator.style.borderRadius = '50%';
      colorIndicator.style.backgroundColor = tag.colorHex; // Colore basato su tag.colorHex
      colorIndicator.style.border = '1px solid #ccc';
      tagContainer.appendChild(colorIndicator);

      // Sezione per il testo del tag
      const tagDetails = document.createElement('div');
      tagDetails.className = 'd-flex flex-column ms-3';

      // Testo principale del tag
      const tagText = document.createElement('span');
      tagText.textContent = tag.tagValue;
      tagText.className = 'fw-bold'; // Testo in grassetto
      tagDetails.appendChild(tagText);

      // Informazione sul creatore del tag
      const tagCreator = document.createElement('span');
      tagCreator.textContent = `Created by: ${tag.createdBy}`;
      tagCreator.className = 'text-muted text-small'; // Testo più piccolo e opaco
      tagDetails.appendChild(tagCreator);

      tagContainer.appendChild(tagDetails);

      // Aggiunta del contenitore del tag alla sidebar
      sidebarContainer.appendChild(tagContainer);
    }
  }

}

function getExplainingText(text) {
  const explanationContainer = document.createElement('div');
  explanationContainer.className = 'p-3 mb-3 rounded';
  explanationContainer.style.backgroundColor = '#4d6a7b'; // Matching background color

  const explanation = document.createElement('p');
  explanation.textContent = text;
  explanation.style.textAlign = 'center'; // Center the text
  explanation.style.fontSize = '1.2em'; // Increase the font size

  explanationContainer.appendChild(explanation);
  return explanationContainer;
}

async function stopTaggingOperation(artifactId) {
  return fetch(`/api/v1/artifact/${artifactId}/stoptagging`, {
    method: 'PUT',
    headers: {
      'Authorization': `Bearer ${localStorage.getItem('authToken')}`
    }
  }).then(response => {
    if (response.ok) {
      return response.json().then(data => {
        return data.msg;
      });
    } else {
      return response.json().then(errorData => {
        console.error("Error message: " + errorData.msg);
        alert("Error: " + errorData.msg);
        throw new Error(errorData.msg);
      });
    }
  });
}

async function startTaggingOperation(artifactId) {
  return fetch(`/api/v1/artifact/${artifactId}/starttagging`, {
    method: 'PUT',
    headers: {
      'Authorization': `Bearer ${localStorage.getItem('authToken')}`
    }
  }).then(response => {
    if (response.ok) {
      return response.json().then(data => {
        return data.msg;
      });
    } else {
      return response.json().then(errorData => {
        console.error("Error message: " + errorData.msg);
        alert("Error: " + errorData.msg);
        throw new Error(errorData.msg);
      });
    }
  });
}

function populateSidebarUser(artifactId, username) {
  const tagDropDown = document.getElementById('tagList');
  const confirmButton = document.getElementById('confirmTagsButton');
  const clearButton = document.getElementById('clearTextAreaButton');

  fetch('/api/v1/tag/byuser/' + localStorage.getItem('username') + '/all', {
    method: 'GET',
    headers: {
      'Authorization': `Bearer ${localStorage.getItem('authToken')}`
    }
  })
  .then(response => {
    if (response.ok) {
      return response.json().then(data => {
        let tags = data.tags;

        tags = removeDuplicates(tags, 'tagValue');

        if (Array.isArray(tags) && tags.length > 0) {
          showElementsInsideDropdown(tagDropDown, tags, true, tagIcon,
              'tagValue');
        } else {
          const noTags = {tagValue: "No tags Available"};
          showElementsInsideDropdown(tagDropDown, [noTags], false, null,
              'tagValue');
        }
      });
    } else {
      return response.json().then(errorData => {
        console.error("Something went wrong:", errorData.msg);
        alert("Errore: " + errorData.msg);
      });
    }
  })
  .catch(error => {
    console.error("Si è verificato un errore:", error.message);
    alert("Errore: " + error.message);
  });

  populateExistingTags(artifactId, username);

  // when the user clicks the confirm button
  confirmButton.addEventListener('click', () => {
    const tags = tagInput.value.split(',').map(tag => tag.trim()).filter(
        tag => tag.length > 0);

    if (tags.length === 0) {
      alert('Please add at least one tag');
      return;
    }

    const defaultHex = '#000000';   // TODO: pick dynamically
    const userId = localStorage.getItem('user-userId');
    const artifactId = window.location.pathname.split('/')[2];

    saveTags(artifactId, userId, defaultHex, tags);
  });

  clearButton.addEventListener('click', () => {
    if (confirm('Are you sure you want to clear the tags?')) {
      const tagInput = document.getElementById('tagInput');
      tagInput.value = '';
    }
  });

}

function removeDuplicates(array, key) {
  const uniqueTags = [];
  const tagValues = new Set();

  array.forEach(tag => {
    if (!tagValues.has(tag[key])) {
      tagValues.add(tag[key]);
      uniqueTags.push(tag);
    }
  });

  return uniqueTags;
}

function populateExistingTags(artifactId, username) {
  const existingTagList = document.getElementById('existingTagsList');

  fetchTagsFromUser(artifactId, username).then(tags => {
    console.log("tags:", tags.tags);
    // tagInput.value = tags.tags.map(tag => tag.tagValue).join(', ');
    existingTagList.innerHTML = ''; // Clear existing tags
    tags.tags.forEach(tag => {
      const listItem = document.createElement('li');
      listItem.textContent = tag.tagValue;

      const removeButton = document.createElement('button');
      removeButton.textContent = 'x';
      removeButton.style.marginLeft = '10px';
      removeButton.addEventListener('click', () => {
        // listItem.remove();
        // Optionally, you can add code here to remove the tag from the server
        if (confirm(
            `Are you sure you want to remove the tag "${tag.tagValue}"?`)) {
          deleteTagById(tag.tagId).then(() => {
            alert(`Tag "${tag.tagValue}" removed successfully.`);
            window.location.reload();
          });
        }
      });

      listItem.appendChild(removeButton);
      existingTagList.appendChild(listItem);
    });
  })
}

// 2. visualize the file
function displayArtifact(blob, contentType, container) {
  if (contentType.startsWith('text') || contentType.includes('javascript')
      || contentType.includes('java')) {
    const reader = new FileReader();
    reader.onload = () => {
      const code = document.createElement('code');
      code.className = detectLanguage(contentType); // determine the language
      code.textContent = reader.result;

      const pre = document.createElement('pre');
      pre.appendChild(code);

      container.innerHTML = '';       // Clear existing content
      container.appendChild(pre);     // Add the new content

      hljs.highlightElement(code);    // Highlight the code wih Highlight.js
    };

    reader.readAsText(blob);
  } else if (contentType.startsWith('image')) {
    // if the file is an image
    const img = document.createElement('img');
    img.src = URL.createObjectURL(blob);
    img.alt = 'Artifact Image';
    img.style.maxWidth = '100%';
    container.innerHTML = '';
    img.style.borderRadius = '15px';
    img.style.boxShadow = '0 8px 16px rgba(0, 0, 0, 0.2)';
    container.appendChild(img);
  } else if (contentType === 'application/pdf') {
    // if the file is a pdf
    const object = document.createElement('object');
    object.data = URL.createObjectURL(blob);
    object.type = 'application/pdf';
    object.width = '100%';
    object.height = '500px';
    container.innerHTML = '';
    container.appendChild(object);
  } else {
    // if the file is not text, image or pdf visualize a download link
    const containerDiv = document.createElement('div');
    containerDiv.className = 'd-flex flex-column align-items-center';

    const fileTypeText = document.createElement('p');
    fileTypeText.textContent = `This is a ${contentType.split(
        '/')[1]} file. You cannot preview it.`;

    const downloadButton = document.createElement('a');
    downloadButton.href = URL.createObjectURL(blob);
    downloadButton.download = 'artifact_file';
    downloadButton.className = 'btn btn-primary d-flex align-items-center';
    downloadButton.innerHTML = `
    <svg xmlns="http://www.w3.org/2000/svg" width="1em" height="1em" fill="currentColor" class="bi bi-file-earmark-arrow-down" viewBox="0 0 16 16" style="margin-right: 1em">
  <path d="M8.5 6.5a.5.5 0 0 0-1 0v3.793L6.354 9.146a.5.5 0 1 0-.708.708l2 2a.5.5 0 0 0 .708 0l2-2a.5.5 0 0 0-.708-.708L8.5 10.293z"/>
  <path d="M14 14V4.5L9.5 0H4a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h8a2 2 0 0 0 2-2M9.5 3A1.5 1.5 0 0 0 11 4.5h2V14a1 1 0 0 1-1 1H4a1 1 0 0 1-1-1V2a1 1 0 0 1 1-1h5.5z"/>
</svg>
    Download
  `;

    container.innerHTML = '';
    containerDiv.appendChild(fileTypeText);
    containerDiv.appendChild(downloadButton);
    container.appendChild(containerDiv);
  }
}

/**
 * Show elements inside a dropdown.
 *
 * @param targetDropdown the dropdown where the elements will be shown
 * @param list the list of elements to show
 * @param clickable if the elements are clickable
 * @param icon the icon to show before the text
 * @param attributeName the name of the attribute to show inside the dropdown
 */
function showElementsInsideDropdown(targetDropdown,
    list,
    clickable = false,
    icon = defaultIcon,
    attributeName
) {
  list.forEach(listItem => {
    // Creating an element <li> for each team
    const wrapperItem = document.createElement('li');

    // Creating the <a> element
    const link = document.createElement('a');

    link.style.textDecoration = 'none';
    link.style.color = 'inherit'; // To inherit the color from the parent

    // Creating SVG icon
    const iconElement = document.createElement('span');
    iconElement.innerHTML = icon;
    iconElement.style.marginLeft = '1em';
    iconElement.style.marginRight = '1em';

    // Adding SVG icon to <a>
    link.appendChild(iconElement);

    // Adding team
    const textToShow = document.createElement('span');
    textToShow.textContent = listItem[attributeName];
    textToShow.style.marginLeft = '0.3em';

    // Adding artifactName to <a>
    link.appendChild(textToShow);

    // Adding <a> to <li>
    wrapperItem.appendChild(link);

    // Adding <li> to dropdown
    targetDropdown.appendChild(wrapperItem);

    if (clickable) {
      link.addEventListener('click', () => {
        const tagInput = document.getElementById('tagInput');
        const currentTags = tagInput.value.split(',').map(
            tag => tag.trim()).filter(tag => tag.length > 0);
        if (!currentTags.some(tag => tag.toLowerCase()
            === listItem[attributeName].toLowerCase())) {
          tagInput.value = currentTags.join(', ') + (currentTags.length ? ', '
              : '') + listItem[attributeName];
        }
      });
    }
  });
}