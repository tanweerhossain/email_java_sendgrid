public class Body {
    private Boolean status;
    private String message;
    private String createdBy;
    private String createdDate;
    private String sendgridId;
    private Transaction transaction;

    public Boolean getStatus() {
      return status;
    }
    public void setStatus(Boolean status) {
      this.status = status;
    }
    public String getCreatedBy() {
      return createdBy;
    }
    public void setCreatedBy(String createdBy) {
      this.createdBy = createdBy;
    }
    public String getCreatedDate() {
      return createdDate;
    }
    public void setCreatedDate(String createdDate) {
      this.createdDate = createdDate;
    }
    public String getSendgridId() {
      return sendgridId;
    }
    public void setSendgridId(String sendgridId) {
      this.sendgridId = sendgridId;
    }
    public Transaction getTransaction() {
      return transaction;
    }
    public void setTransaction(Transaction transaction) {
      this.transaction = transaction;
    }
    public String getMessage() {
      return message;
    }
    public void setMessage(String message) {
      this.message = message;
    }
}