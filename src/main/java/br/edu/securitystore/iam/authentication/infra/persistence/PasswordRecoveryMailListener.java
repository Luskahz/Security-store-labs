package br.edu.securitystore.iam.authentication.infra.persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class PasswordRecoveryMailListener {
    private static final Logger log=LoggerFactory.getLogger(PasswordRecoveryMailListener.class);
    private final ObjectProvider<JavaMailSender> mailProvider;
    private final String host;
    public PasswordRecoveryMailListener(ObjectProvider<JavaMailSender> mailProvider,
            @Value("${spring.mail.host:}") String host){this.mailProvider=mailProvider;this.host=host;}

    @Async
    @TransactionalEventListener(phase=TransactionPhase.AFTER_COMMIT)
    public void send(PasswordRecoveryMailEvent event){
        if(host==null||host.isBlank()){
            log.info("SMTP não configurado; e-mail de recuperação não enviado");
            return;
        }
        try{JavaMailSender mail=mailProvider.getIfAvailable();if(mail==null){log.warn("SMTP configurado, mas o serviço de e-mail não está disponível");return;}SimpleMailMessage message=new SimpleMailMessage();message.setFrom(event.from());message.setTo(event.email());message.setSubject("Redefinição de senha — Security Store");message.setText("Recebemos uma solicitação para redefinir sua senha. Use este link em até "+event.expiresInMinutes()+" minutos:\n\n"+event.link()+"\n\nSe você não solicitou, ignore esta mensagem.");mail.send(message);}
        catch(RuntimeException error){log.warn("Password recovery email could not be sent; check SMTP configuration");}
    }
}
