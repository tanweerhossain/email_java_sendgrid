import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;

import javax.activation.MailcapCommandMap;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import com.sendgrid.*;

class Content {
	public String type ;
	public String value ;
}
class Personalizations {
	public String[] to;
	public String[] cc;
	public String[] bcc;
	public String subject;
}
class Transaction {
	public Personalizations[] personalizations;
	public String from;
	public String reply_to;
  public String subject;
	public Content[] content;
}
class Body {
    private Boolean status;
    private String statusDescription;
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
	public String getStatusDescription() {
		return statusDescription;
	}
	public void setStatusDescription(String statusDescription) {
		this.statusDescription = statusDescription;
	}
}

class Status {
	private Boolean status;
	private String statusDescription;
	
	public Boolean getStatus() {
		return status;
	}
	public void setStatus(Boolean status) {
		this.status = status;
	}
	public String getStatusDescription() {
		return statusDescription;
	}
	public void setStatusDescription(String statusDescription) {
		this.statusDescription = statusDescription;
	}
}
public class JsonParserServlet extends HttpServlet {
		private String SENDGRID_API_KEY = "NULL";
		
		private String sendMail(Transaction transaction){
	

			//Mail sending format calls
			Mail mail = new Mail();
			mail.from = new Email(transaction.from);
			mail.replyTo = new Email(transaction.reply_to);
			mail.subject = transaction.subject;
			for( int i = 0 ; i < transaction.content.length ; ++i )
				mail.addContent( new com.sendgrid.Content( transaction.content[i].type, transaction.content[i].value ));
			for( int i = 0 ; i < transaction.personalizations.length ; ++i ){
				Personalization personalization = new Personalization();
				for( int j = 0 ; j < transaction.personalizations[i].to.length ; ++j )
					personalization.addTo( new Email(transaction.personalizations[i].to[j]));
				for( int j = 0 ; j < transaction.personalizations[i].bcc.length ; ++j )
					personalization.addBcc( new Email(transaction.personalizations[i].bcc[j]));
				for( int j = 0 ; j < transaction.personalizations[i].cc.length ; ++j )
					personalization.addCc( new Email(transaction.personalizations[i].cc[j]));
				personalization.setSubject(transaction.personalizations[i].subject);
				mail.addPersonalization(personalization);
			}



			SendGrid sg = new SendGrid(SENDGRID_API_KEY);
			Request request = new Request();
			Response response = new Response();
			try {
				request.method = (Method.POST);
				request.endpoint = ("mail/send");
				request.body = (mail.build());
				response = sg.api(request);
				System.out.println(response.statusCode);
				System.out.println(response.body);
				System.out.println(response.headers);
			} catch (IOException ex) {
				return ex.toString();
			}
			return new Gson().toJson("{ \"status\": "+ response.statusCode + ",\n\"header\": "+ response.headers + ",\n\"message\": " +"Email sent successfully\n }");
		}
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");       
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
				Gson gson = new Gson();
				
				//Taking sendgrid key value from frontend
				SENDGRID_API_KEY = request.getHeader("authorization").split(" ")[1];
        try {
					StringBuilder sb = new StringBuilder();
					String s;

					//convert json data to string format
					while ((s = request.getReader().readLine()) != null) {
							sb.append(s);
					}
					
					//changing json stringify data to object fromat 
					Transaction object = (Transaction) gson.fromJson(sb.toString(), Transaction.class);
					
					//calling mail sending API
					String responseString = new String(sendMail(object));

					//returning status to frontend
					out.print(responseString);
					out.flush();
        }catch (Exception ex) {
					ex.printStackTrace();
					Status status = new Status();
					status.setStatusDescription(ex.getMessage());
					status.setStatus(false);
					out.print(gson.toJson(status));
					out.flush();
        }
    }
}


