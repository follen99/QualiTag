export class LoadingModal {
  constructor(defaultHref, defaultMessage) {
    this.defaultHref = defaultHref;
    this.defaultMessage = defaultMessage;
    this.showLoading = true;
    this.done = false;

    this.loadingModal = new bootstrap.Modal(
        document.getElementById('loadingModal'));
    this.modalMessage = document.getElementById('modalMessage');
    this.loadingWheel = document.getElementById('loadingWheel');
    this.closeModalButton = document.getElementById('closeModalButton');

    this.closeModalButton.style.display = 'none'; // default value
    this.closeModalButton.addEventListener('click', function () {
      this.loadingModal.hide();

      if (this.done) {
        window.location.href = '/project/' + localStorage.getItem('username')
            + "/projects";
        return;
      }

      // restoring default values
      this.loadingWheel.style.display = 'block';     // showing loading wheel
      this.closeModalButton.style.display = 'none';  // hiding close button

      this.modalMessage.innerText = this.defaultMessage; // setting default message
    });
  }

  showLoadingWheel(show) {
    if (show) {
      this.loadingWheel.style.display = 'block';
    } else {
      this.loadingWheel.style.display = 'none';
    }
  }

  showCloseButton(show) {
    if (show) {
      this.closeModalButton.style.display = 'block';
    } else {
      this.closeModalButton.style.display = 'none';
    }
  }

  isDone(done) {
    this.done = done;
  }

  setInnerMessage(message) {
    this.defaultMessage = message;
  }

  showModal(message, showLoadingWheel, showCloseButton, done) {
    if (showLoadingWheel !== null && showLoadingWheel !== undefined) {
      this.showLoadingWheel(showLoadingWheel);
    } else this.showLoadingWheel(true); // default value

    if (showCloseButton !== null && showCloseButton !== undefined) {
      this.showCloseButton(showCloseButton);
    } else this.showCloseButton(false); // default value

    if (done !== null && done !== undefined) {
      this.isDone(done);
    }

    this.modalMessage.innerText = message || this.defaultMessage;
    this.loadingModal.show();
  }

  hideModal() {
    this.loadingModal.hide();

    if (this.done) {
      window.location.href = this.defaultHref;
    }
  }
}