package com.aschroder.xero;

import java.util.Scanner;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

/**
 * The below example shows how the Xero oAuth public application 
 * works using the Scribe oauth library.
 * 
 * If you are using the application in a servlet/web environment you can pass 
 * a callback url like so:
 * 
 * OAuthService service = new ServiceBuilder()
 *			.provider(XeroApi.class)
 *			.apiKey(API_KEY)
 *			.apiSecret(API_SECRET)
 *			.callback(CALLBACK_URL)
 *			.build();
 *			
 * The CALLBACK_URL host must have been registered 
 * with Xero when creating you public application.
 * 
 * The callback URL will then be accessed with the GET parameter:
 * oauth_verifier
 * 
 * You can use this value in Scribe like so:
 * Token accessToken = 
 * 		service.getAccessToken(requestToken, 
 * 			new Verifier(req.getParameter("oauth_verifier")));
 * 
 * (the request token should be persisted and used between the 
 * initial oauth request and the callback)
 * 
 * @author Ashley Schroder
 *
 */
public class XeroExample {
	
	private static final String PROTECTED_RESOURCE_URL = "https://api.xero.com/api.xro/2.0/Accounts";
	// TODO: put your own values here
	private static String API_KEY = "";
	private static String API_SECRET = "";

	public static void main(String[] args) {
		
		Scanner in = new Scanner(System.in);

		if(API_KEY.isEmpty() ||
				API_SECRET.isEmpty()) {
			
			System.out.println("Missing consumer key/secret, please input below");
			System.out.print(">>");
			API_KEY = in.nextLine();
			System.out.println("Now enter consumer secret below");
			System.out.print(">>");
			API_SECRET = in.nextLine();

		}
		
		OAuthService service = new ServiceBuilder()
		.provider(XeroApi.class)
		.apiKey(API_KEY)
		.apiSecret(API_SECRET)
		.build();
		
		System.out.println("=== Xero OAuth Workflow ===");
		System.out.println();

		// Obtain the Request Token
		System.out.println("Fetching the Request Token...");
		Token requestToken = service.getRequestToken();
		System.out.println("Got the Request Token!");
		System.out.println();

		System.out.println("Now go and authorize Scribe here:");
		System.out.println(service.getAuthorizationUrl(requestToken));
		System.out.println("And paste the verifier here");
		System.out.print(">>");
		Verifier verifier = new Verifier(in.nextLine());
		System.out.println();

		// Trade the Request Token and Verfier for the Access Token
		System.out.println("Trading the Request Token for an Access Token...");
		Token accessToken = service.getAccessToken(requestToken, verifier);
		System.out.println("Got the Access Token!");
		System.out.println("(if your curious it looks like this: "
				+ accessToken + " )");
		System.out.println();

		// Now let's go and ask for a protected resource!
		System.out.println("Now we're going to access a protected resource...");
		OAuthRequest request = new OAuthRequest(Verb.GET,
				PROTECTED_RESOURCE_URL);
		service.signRequest(accessToken, request);
		Response response = request.send();
		System.out.println("Got it! Lets see what we found...");
		System.out.println();
		System.out.println(response.getBody());

		System.out.println();
		System.out
				.println("Thats it man! Go and build something awesome with Scribe! :)");
	}

}