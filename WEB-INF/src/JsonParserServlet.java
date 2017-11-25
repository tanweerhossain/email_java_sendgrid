import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.io.PrintWriter;

import javax.activation.MailcapCommandMap;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import com.sendgrid.*;

public class JsonParserServlet extends HttpServlet {
		private String SENDGRID_API_KEY = "NULL";
		private void validateMailFormat(String email) throws Exception{
			int posAtTheRate = email.indexOf("@");
			int posDot = email.lastIndexOf(".");
			if( !( ( posAtTheRate>0 && posAtTheRate<email.length()-3  )&&( posDot>(posAtTheRate+1) && posDot<email.length()-1 ) ) )
				throw new Exception("Email format is not valid.");
		}
		private void senderOnlyToPresent(Personalizations personalizations,Personalization personalization) throws Exception{
			for( int j = 0 ; j < personalizations.to.length ; ++j ){
				validateMailFormat(personalizations.to[j]);
				personalization.addTo( new Email(personalizations.to[j]));
			}
		}
		private void senderOnlyCcPresent(Personalizations personalizations,Personalization personalization) throws Exception{
			for( int j = 0 ; j < personalizations.cc.length ; ++j ){
				validateMailFormat(personalizations.cc[j]);
				personalization.addCc( new Email(personalizations.cc[j]));
			}
		}
		private void senderOnlyBccPresent(Personalizations personalizations,Personalization personalization) throws Exception{
			for( int j = 0 ; j < personalizations.bcc.length ; ++j ){
				validateMailFormat(personalizations.bcc[j]);
				personalization.addBcc( new Email(personalizations.bcc[j]));
			}
		}
		private void senderOnlyToAbsent(Personalizations personalizations,Personalization personalization) throws Exception{
			for( int j = 0 ; j < personalizations.bcc.length ; ++j ){
				validateMailFormat(personalizations.bcc[j]);
				personalization.addBcc( new Email(personalizations.bcc[j]));
			}
			for( int j = 0 ; j < personalizations.cc.length ; ++j ){
				validateMailFormat(personalizations.cc[j]);
				personalization.addCc( new Email(personalizations.cc[j]));
			}
		}
		private void senderOnlyCcAbsent(Personalizations personalizations,Personalization personalization) throws Exception{
			for( int j = 0 ; j < personalizations.to.length ; ++j ){
				validateMailFormat(personalizations.to[j]);
				personalization.addTo( new Email(personalizations.to[j]));
			}
			for( int j = 0 ; j < personalizations.bcc.length ; ++j ){
				validateMailFormat(personalizations.bcc[j]);
				personalization.addBcc( new Email(personalizations.bcc[j]));
			}
		}
		private void senderOnlyBccAbsent(Personalizations personalizations,Personalization personalization) throws Exception{
			for( int j = 0 ; j < personalizations.to.length ; ++j ){
				validateMailFormat(personalizations.to[j]);
				personalization.addTo( new Email(personalizations.to[j]));
			}
			for( int j = 0 ; j < personalizations.cc.length ; ++j ){
				validateMailFormat(personalizations.cc[j]);
				personalization.addCc( new Email(personalizations.cc[j]));
			}
		}
		private String senderAllPresent(Transaction transaction)throws Exception{
			
			//Mail sending format calls
			Mail mail = new Mail();
			mail.from = new Email(transaction.from);
			mail.replyTo = new Email(transaction.reply_to);
			mail.subject = transaction.subject;
			for( int i = 0 ; i < transaction.content.length ; ++i ){
				if( transaction.content[i].type == null || transaction.content[i].value == null || transaction.content[i].type.length() == 0 || transaction.content[i].value.length() == 0 )
					throw new Exception("Content is missing or content format is wrong");
				mail.addContent( new com.sendgrid.Content( transaction.content[i].type, transaction.content[i].value ));
			}
			for( int i = 0 ; i < transaction.personalizations.length ; ++i ){
				Personalization personalization = new Personalization();

				//All sender addresses are present
				if( transaction.personalizations[i].to != null  && 
						transaction.personalizations[i].bcc != null &&
						transaction.personalizations[i].cc != null ) {
						for( int j = 0 ; j < transaction.personalizations[i].to.length ; ++j ){
							validateMailFormat(transaction.personalizations[i].to[j]);
							personalization.addTo( new Email(transaction.personalizations[i].to[j]));
						}
						for( int j = 0 ; j < transaction.personalizations[i].bcc.length ; ++j ){
							validateMailFormat(transaction.personalizations[i].bcc[j]);
							personalization.addBcc( new Email(transaction.personalizations[i].bcc[j]));
						}
						for( int j = 0 ; j < transaction.personalizations[i].cc.length ; ++j ){
							validateMailFormat(transaction.personalizations[i].cc[j]);
							personalization.addCc( new Email(transaction.personalizations[i].cc[j]));
						}
				}	//Only bcc is present
				else if( transaction.personalizations[i].to == null  && 
								transaction.personalizations[i].bcc != null &&
								transaction.personalizations[i].cc == null ){
						senderOnlyBccPresent(transaction.personalizations[i], personalization);
				}//Only cc is present
				else if( transaction.personalizations[i].to == null  && 
								transaction.personalizations[i].bcc == null &&
								transaction.personalizations[i].cc != null ){
						senderOnlyCcPresent(transaction.personalizations[i], personalization);
				}//Only to is present
				else if( transaction.personalizations[i].to != null  && 
								transaction.personalizations[i].bcc == null &&
								transaction.personalizations[i].cc == null ){
						senderOnlyToPresent(transaction.personalizations[i], personalization);
				}//Only to is absent
				else if( transaction.personalizations[i].to == null  && 
								transaction.personalizations[i].bcc != null &&
								transaction.personalizations[i].cc != null ){
						senderOnlyToAbsent(transaction.personalizations[i], personalization);
				}//Only cc is absent
				else if( transaction.personalizations[i].to != null  && 
								transaction.personalizations[i].bcc != null &&
								transaction.personalizations[i].cc == null ){
						senderOnlyCcAbsent(transaction.personalizations[i], personalization);
				}//Only bcc is absent
				else if( transaction.personalizations[i].to != null  && 
								transaction.personalizations[i].bcc == null &&
								transaction.personalizations[i].cc != null ){
						senderOnlyBccAbsent(transaction.personalizations[i], personalization);
				}
				else{
					throw new Exception("Receiver email is missing");
				}
				if(transaction.personalizations[i].subject == null && transaction.subject == null )
					throw new Exception("Email subject is missing.");
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
			Status status = new Status();

			status.setHeader(response.headers.toString());
			if( response.statusCode == 202 )
				status.setStatus(true);
			else
				status.setStatus(false); 
			status.setMessage("Email sent successfully");
			return new Gson().toJson(status);
		}
		private String sendMailWithValidation(Transaction transaction) throws Exception{
			//Check availability of transaction
			if( transaction == null )
				throw new Exception("Email format is bad");

			//check from availability
			if( transaction.from == null )
				throw new Exception("Sender email is missing");

			//validate from mail format
			validateMailFormat(transaction.from);

			//reply_to is not present
			if( transaction.reply_to == null )
				transaction.reply_to = transaction.from;

			//validate reply_to mail format
			validateMailFormat(transaction.reply_to);

			//validate personalizations
			if( transaction.personalizations == null || transaction.personalizations.length == 0 )
				throw new Exception("Receiver email address is missing.");

			//content is missing
			if( transaction.content == null || transaction.content.length == 0 )
				throw new Exception("Content is missing");

			return senderAllPresent(transaction);
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
					String responseString = new String(sendMailWithValidation(object));
					
					//returning status to frontend
					out.print(responseString);
					out.flush();
        }
				catch (Exception ex) {
					ex.printStackTrace();
					Status status = new Status();
					status.setMessage(ex.getMessage());
					status.setStatus(false);
					out.print(gson.toJson(status));
					out.flush();
				}
    }
}


