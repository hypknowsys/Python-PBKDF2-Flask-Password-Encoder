import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.RandomUtils;

import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;

import javax.crypto.Mac;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;






public class PBKDF2PasswordEncoder {

    
    private String currentAlgorithm;
    private String secretFactory;
    private int iterations;
    private int randomSaltLength;
    private int keyLength;
    
    
    public PBKDF2PasswordEncoder() {
        // TODO Auto-generated constructor stub
    }
    
    public String encodePassword(String rawPassword, String securityPasswordSalt                           ,  String algorithm) throws Exception, NoSuchAlgorithmException {
        
        
        
        if (algorithm.equals("pbkdf2-sha512")) {
            this.currentAlgorithm = "HmacSHA512";
            secretFactory =  "PBKDF2With" + this.currentAlgorithm;
            iterations = 19000;
            randomSaltLength = 16;
            keyLength = 64;
        }
        else {
            throw new NoSuchAlgorithmException();
        }
        
        
        /* create HMAC */
        SecretKeySpec keySpec = new SecretKeySpec(securityPasswordSalt.getBytes(), this.currentAlgorithm);
        Mac mac = Mac.getInstance( this.currentAlgorithm);
        mac.init(keySpec);
        String currentMac = Base64.encodeBase64String(mac.doFinal(rawPassword.getBytes()));
        
        /* create random salt with mac*/
        byte[] randomBytes = RandomUtils.nextBytes(randomSaltLength);
        String randomSalt64Encoded = Base64.encodeBase64String(randomBytes);
        
//        if (randomSalt64Encoded.endsWith("==")) {
//            randomSalt64Encoded = randomSalt64Encoded.substring(1, randomSalt64Encoded.length() - 2);
//        }
//        
        

        
        /* create PBKDF2 */
        KeySpec spec = new PBEKeySpec(currentMac.toCharArray(), randomBytes, iterations, keyLength * 8);
        SecretKeyFactory f = SecretKeyFactory.getInstance(secretFactory);
    
        String finalPBKDF2 = Base64.encodeBase64String(f.generateSecret(spec).getEncoded());
        
//        if (finalPBKDF2.endsWith("==")) {
//            finalPBKDF2 = finalPBKDF2.substring(1, finalPBKDF2.length() - 2);
//        }
//        
        
        
        StringBuffer tmpStringBuffer = new StringBuffer();
        tmpStringBuffer.append("$").append(algorithm).append("$")
            .append(this.iterations).append("$").append(randomSalt64Encoded).append("$").append(finalPBKDF2 );
        
        
        return tmpStringBuffer.toString();
        
    }
    
    
    
    public static void main(String [] args) {
        
        PBKDF2PasswordEncoder myPBKDF2PasswordEncoder = new PBKDF2PasswordEncoder();
        
        
        try {
            System.out.println(myPBKDF2PasswordEncoder.encodePassword("arschloch","6e95b1ed-a8c3-4da0-8bac-6fcb11c39ab4", "pbkdf2-sha512"));
        }
        
        catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    
    
    

}
