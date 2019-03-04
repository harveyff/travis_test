package utils;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;

import redis.RedisUtil;
import servlet.StartServlet;

public class AmazonSESUtil {

	static final String FROM = "harvey@bytetrade.io";
	static  String URL="https://www.coinnewton.com";
	
	public static Logger log4j = LogManager.getLogger(AmazonSESUtil.class);
	
	public static void init(){
		if(Utils.isDug){
			 URL="https://nt-test.bytetrade.io";
		}
	}
	public static boolean sendTextEmail(String type, String email,String lang) {
		log4j.info(new Date()+",sendTextEmail,type:"+type+",email:"+email+",lang:"+lang);
		email=email.replaceAll(" ", "");
		try {
			String code = RedisUtil.getKV(type+"_" + email);
			if(Utils.isStringEmpty(code)){
				code= Utils.getUUID();
			}
			String SUBJECT = "";
			String HTMLBODY = "";
			if(lang.equals("zhCN")){
                if (type.equals("register")||type.equals("reregister")) {
                    SUBJECT = "CoinNewton用户激活";
                    String link = "</br><a ses:no-track href='"+URL+"/register.html?email="+email+"&code="+code+"'>"+URL+"/register.html?email="
                            + email + "&code=" + code + "</a>";
                    HTMLBODY = "尊敬的用户您好，欢迎来到CoinNewton，这是一封激活邮件。</br></br>请点击以下链接完成激活，如果点击后网页无法访问，请手动复制该链接到浏览器窗口进行访问。</br>" + link;
                } else if (type.equals("forgot")) {
                    SUBJECT = "CoinNewton重置登录密码";
                    String link = "</br><a ses:no-track href='"+URL+"/reset.html?email="+email+"&code="+code+"'>"+URL+"/reset.html?email="
                            + email + "&code=" + code + "</a>";
                    HTMLBODY = "尊敬的用户您好，欢迎来到CoinNewton，这是一封重置登录密码的邮件。</br></br>请点击以下链接进行重置，如果点击后网页无法访问，请手动复制该链接到浏览器窗口进行访问。</br></br>" + link + "</br></br>如果此活动不是您本人操作，请尽快修改您的邮箱密码并重置CoinNewton密码。";
                }else if (type.equals("forgot_capital")) {
                    SUBJECT = "CoinNewton重置资金密码";
                    String link = "</br><a ses:no-track href='"+URL+"/reset.html?reset=1&email="+email+"&code="+code+"'>"+URL+"/reset.html?reset=1&email="
                            + email + "&code=" + code + "</a>";
                    HTMLBODY = "尊敬的用户您好，欢迎来到CoinNewton，这是一封重置登录密码的邮件。</br></br>请点击以下链接进行重置，如果点击后网页无法访问，请手动复制该链接到浏览器窗口进行访问。</br></br>" + link + "</br></br>如果此活动不是您本人操作，请尽快修改您的邮箱密码并重置CoinNewton密码。";
                }
                HTMLBODY+="<br/><br/>CoinNewton团队<br/>系统邮件，请勿回复";
			}else if(lang.equals("tr")){
                if (type.equals("register")||type.equals("reregister")) {
                    SUBJECT = "CoinNewton kullanıcı aktivasyonu";
                    String link = "</br><a ses:no-track href='"+URL+"/register.html?email="+email+"&code="+code+"'>"+URL+"/register.html?email="
                            + email + "&code=" + code + "</a>";
                    HTMLBODY = "Sevgili Kullanıcı, CoinNewton'a hoş geldiniz, bu bir aktivasyon e-postasıdır.</br></br>Aktivasyonu tamamlamak için lütfen aşağıdaki linke tıklayınız. Sayfaya tıklandıktan sonra erişilemiyorsa, lütfen erişimi kaldırmak için bağlantıyı tarayıcı penceresine el ile kopyalayın.</br>" + link;
                } else if (type.equals("forgot")) {
                    SUBJECT = "CoinNewton giriş şifresini sıfırladı";
                    String link = "</br><a ses:no-track href='"+URL+"/reset.html?email="+email+"&code="+code+"'>"+URL+"/reset.html?email="
                            + email + "&code=" + code + "</a>";
                    HTMLBODY = "Sayın Kullanıcı, CoinNewton'a hoş geldiniz, bu, giriş şifrenizi sıfırlamak için bir e-postadır.</br></br>Sıfırlamak için lütfen aşağıdaki bağlantıyı tıklayın. Sayfaya tıklandıktan sonra erişilemiyorsa, lütfen erişimi kaldırmak için bağlantıyı tarayıcı penceresine el ile kopyalayın.</br></br>" + link + "</br></br>Bu etkinlik sizin değilse, lütfen e-posta şifrenizi değiştirin ve en kısa zamanda CoinNewton şifrenizi sıfırlayın.";
                }else if (type.equals("forgot_capital")) {
                    SUBJECT = "CoinNewton giriş şifresini sıfırladı";
                    String link = "</br><a ses:no-track href='"+URL+"/reset.html?reset=1&email="+email+"&code="+code+"'>"+URL+"/reset.html?reset=1&email="
                            + email + "&code=" + code + "</a>";
                    HTMLBODY = "Sayın Kullanıcı, CoinNewton'a hoş geldiniz, bu, giriş şifrenizi sıfırlamak için bir e-postadır.</br></br>Sıfırlamak için lütfen aşağıdaki bağlantıyı tıklayın. Sayfaya tıklandıktan sonra erişilemiyorsa, lütfen erişimi kaldırmak için bağlantıyı tarayıcı penceresine el ile kopyalayın.</br></br>" + link + "</br></br>Bu etkinlik sizin değilse, lütfen e-posta şifrenizi değiştirin ve en kısa zamanda CoinNewton şifrenizi sıfırlayın.";
                }
                HTMLBODY+="<br/><br/>CoinNewton takımı<br/>Sistem postası, lütfen cevap vermeyin";
			}
			AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard()
					// Replace US_WEST_2 with the AWS Region you're using for
					// Amazon SES.
					.withRegion(Regions.US_EAST_1).build();
			SendEmailRequest request = new SendEmailRequest().withDestination(new Destination().withToAddresses(email))
					.withMessage(new Message()
							.withBody(new Body().withHtml(new Content().withCharset("UTF-8").withData(HTMLBODY)))
							.withSubject(new Content().withCharset("UTF-8").withData(SUBJECT)))
					.withSource(FROM).withConfigurationSetName("newton");;
			// Comment or remove the next line if you are not using a
			// configuration set
			// .withConfigurationSetName(CONFIGSET);
			client.sendEmail(request);
			// 写入ES
			RedisUtil.setKV(type + "_" + email, 60 * 60 * 12, code);
			return true;
		} catch (Exception ex) {
			log4j.info("The email was not sent. Error message: " + ex.getMessage());
			return false;
		}
	}
}