package bgu.spl.net.srv.Frames;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class CONNECT extends FrameImpl {

    private String Version;
    private String Host;
    private String login;
    private String Password;
    public CONNECT(String Name){// int message_ID, int ID_Header, String Destination_Header, String Body){
        super(Name);//message_ID,ID_Header, Destination_Header,Body);
    }

    @Override
    public void PrintAll() {
        System.out.println("CONNECT "+Version+ "\n"+ Host + "\n"+ login+ "\n"+ Password);
    }

    public String getPassword() {
        return Password;
    }

    @Override
    public String getName() {
        return super.getName();
    }

    public String getHost() {
        return Host;
    }

    public String getLogin() {
        return login;
    }

    public String getVersion() {
        return Version;
    }

    public void setHost(String host) {
        Host = host;
    }

    public void setLogin(String login) { this.login = login; }

    public void setPassword(String password) {
        Password = password;
    }

    public void setVersion(String version) {
        Version = version;
    }

    @Override
    public byte[] encode(Object message) {
        String tmp = Name + "\n";
        if(!Version.equals(""))
            tmp = tmp + Version + "\n";
        if(!Host.equals(""))
            tmp = tmp + Host + "\n";
        if(!login.equals(""))
            tmp = tmp + login + "\n";
        if(!Password.equals(""))
            tmp = tmp + Password+ "\n";
        tmp = tmp +"\n" + "\u0000";
        byte[] result = tmp.getBytes();
        return result;
    }

    @Override
    public FrameImpl decodeNextByte(byte nextByte) {
        if (nextByte == '\n') {
            String result = new String(bytes, 0, len, StandardCharsets.UTF_8);
            len = 0;
            if(result.contains("version"))
            {
                int index = result.indexOf(":");
                String str = result.substring(index+1);
                this.Version = str;
            }
            else if(result.contains("host"))
            {
                int index = result.indexOf(":");
                String str = result.substring(index+1);
                this.Host = str;
            }
            else if(result.contains("login"))
            {
                int index = result.indexOf(":");
                String str = result.substring(index+1);
                this.login = str;
                setName("login");
            }
            else if(result.contains("passcode"))
            {
                int index = result.indexOf(":");
                String str = result.substring(index+1);
                this.Password = str;
            }
            else
            {
                int index = result.indexOf(":");
                String str = result.substring(index+1);
                this.Password = str;
            }
        }
        else if(nextByte == '\u0000')
        {
            return this;
        }
        else if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
        }
        bytes[len++] = nextByte;
        return this;
    }

}