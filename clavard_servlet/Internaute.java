package clavard_servlet;

import java.net.InetAddress;

/* internaute est la classe utilisée par le servlet pour représenter
 * les utilisateurs de l'application.
 */
public class Internaute {
    private String pseudo_;
    private InetAddress address_;
    private int portTCP_;
    private int portUDP_;
    
    Internaute(String pseudo, InetAddress address, int portTCP, int portUDP) {
        pseudo_=pseudo;
        address_=address;
        portTCP_=portTCP;
        portUDP_=portUDP;
    }
    
    /*on ne renseigne que le port TCP : le correspondant n'a pas besoin
     * du port UDP (non-Javadoc)
     */
    public String toString() {
        System.out.println("Adresse de "+pseudo_+" : "+address_.toString());
        String stringAdresse = address_.toString().replace("/", "");
        return pseudo_+";"+stringAdresse+";"+portTCP_;
    }
    
    public void setPseudo(String pseudo) {pseudo_=pseudo;}
    public String getPseudo() {return pseudo_;}
    public InetAddress getAddress() {return address_;}
    public int getTCPPort() {return portTCP_;}
    public int getUDPPort() {return portUDP_;}
}