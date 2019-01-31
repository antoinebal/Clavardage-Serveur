package clavard_servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class ClavardServlet
 */
public class ClavardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    //lise des internautes connectés
    private ArrayList<Internaute> listeInternautes_;
    
    Scribe scribe_=null;
    
    UDPMulticast udpMCast_=null;
       
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ClavardServlet() {
        super();
        listeInternautes_ = new ArrayList<Internaute>();
        scribe_=new Scribe(this);
        udpMCast_=new UDPMulticast(this);
    }
    
    
    /* on doit : envoyer la liste des connectés au nouveau connecté et informer
     * tous les Internautes de l'arrivée d'un nouveau connecté
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String flagPseudo = request.getParameter("pseudo");
        String flagCo = request.getParameter("connexion");
        String flagDeco = request.getParameter("deconnexion");
        String flagNewPseudo = request.getParameter("newpseudo");
        System.out.println("Pseudo : "+flagPseudo);
        System.out.println("Co : "+flagCo);
        System.out.println("Deco : "+flagDeco);
        System.out.println("NP : "+flagNewPseudo);
        System.out.println(true);
        
        PrintWriter out = response.getWriter() ;
        
        
        //----SUBSCRIBE D'UN NOUVEAU CONNECTE----
        /*requête envoyée par un nouveau connecté :
         * >on vérifie que le pseudo est dispo (sinon on lui
         * en affecte un)
         * >on lui envoie la liste des connectés.
         * >on l'ajoute à la liste des connectés.
         * >on prévient tout le monde
         * de l'arrivée de cet internaute (comment faire?
         * on envoie un udp à chacun?)
         */
        if (flagCo!=null) {
            /*on récupère l'adresse IP et les
             * ports TCP
             */
            String flagIP = request.getParameter("ip");
            String flagPortTCP = request.getParameter("ptcp");
            String flagPortUDP = request.getParameter("pudp");
            
            String pseudoAffecte=flagPseudo;
                //si le pseudo est déjà pris on en affecte un
                if (dejaPris(flagPseudo)) {
                    pseudoAffecte=pseudoDisponible(flagPseudo, 0);
                    System.out.println(pseudoAffecte);
                }
                
                //on envoie la liste          
               out.println(scribe_.construireWelcomeMessage(pseudoAffecte));
               
             //on informe tout le monde
               udpMCast_.multicastMessageUDP(scribe_.construireNewUserMessage(pseudoAffecte, flagPortTCP, flagIP));
                                
                //on remplit la liste des internautes
                ajoutInternaute(pseudoAffecte, flagIP, Integer.parseInt(flagPortTCP), Integer.parseInt(flagPortUDP));
                             
        }
        
        
      //----NOTIFY DE DECONNEXION----
        /* requête envoyée par un internaute qui se
         * déconnecte.
         * >on informe tout le monde du
         * départ de cet internaute
         * >on supprime l'internaute de la liste
         * des connectés
         */
        if (flagDeco!=null) {
        	//on informe tout le monde du départ de cet internaute
        	udpMCast_.multicastMessageUDP(scribe_.construireTchaoMessage(flagPseudo));
        	
        	//on supprime l'internaute de la liste des connectés
        	supprimeInternaute(flagPseudo);
        	
        	//TEST
        	out.println("AUREVOIR "+flagPseudo);
        	out.println(listeCoToString());
        }
        
        //----NOTIFY D'UN CHANGEMENT DE PSEUDO----
        /* requête envoyée par un internaute qui veut
         * changer de pseudo.
         * >on informe tout le monde
         * >on change son nom dans la liste des connectés
         */
        if (flagNewPseudo!=null) {
            //on informe tout le monde de ce chamgement de pseudo
        	udpMCast_.multicastMessageUDP(scribe_.construireNewPseudoMessage(flagPseudo, flagNewPseudo));
        	
        	//on change le pseudo dans la liste des internautes
        	changePseudo(flagPseudo, flagNewPseudo);
        	
        	//TEST
        	out.println(flagPseudo+" TU ES DESORMAIS "+flagNewPseudo);
        	out.println(listeCoToString());
        }
    }
    
    
    
    // utilisé pour envoyer la liste des internautes à un nouveau connecté
    public String listeCoToString() {
        String result="";
        for (Internaute i : listeInternautes_) {
            result=result+":"+i.toString();
        }
        
        return result;
    }
    
    public ArrayList<Internaute> getInternautes() {return listeInternautes_;}
    
    /*construction et ajout d'un internaute dans la liste des connectés avec
     * les paramètres donnés en argument
     */
    public void ajoutInternaute(String pseudo, String stringAddress, int portTCP, int portUDP) {
        try {
            InetAddress address = InetAddress.getByName(stringAddress);
            System.out.println("ajout internaute add : "+address);
            listeInternautes_.add(new Internaute(pseudo, address, portTCP, portUDP));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
    
    /* appelée quand un internaute se déco */
    public void supprimeInternaute(String pseudo) {
        Internaute internaute=null;
        for (Internaute i : listeInternautes_) {
            if (pseudo.equals(i.getPseudo())) {
                internaute=i;
                break;
            }
        }
        listeInternautes_.remove(internaute);
    }
    
    public void changePseudo(String previousPseudo, String newPseudo) {
    	int no=0;
        boolean trouve = false;
        while ((no<listeInternautes_.size()&&(!trouve))) {
            if (listeInternautes_.get(no).getPseudo().equals(previousPseudo)) {
                trouve=true;
                listeInternautes_.get(no).setPseudo(newPseudo);
            }
            no++;
        }
    }
    
    

    /*retourne vrai si le pseudo en argument est déjà pris
     * dans la liste des internautes
     */
  public boolean dejaPris(String pseudo) {
    Iterator<Internaute> it = listeInternautes_.iterator();
    boolean trouve = false;
    while ((it.hasNext())&&(!trouve)) {
        if (it.next().getPseudo().equals(pseudo)) {
            trouve=true;
        }
    }
    return trouve;
  }
 
    
     /* fonction récursive qui retourne un pseudo disponible */
    public String pseudoDisponible(String pseudo, int tail) {
    if (dejaPris(pseudo+tail)) {
        return pseudoDisponible(pseudo, tail+1);
    } else {
        return pseudo+tail;
    }
}

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
        doGet(request, response);
    }

}