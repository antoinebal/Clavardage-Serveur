package clavard_servlet;


/* s'occupe de construire les messages pour le serveur*/
public class Scribe {
	ClavardServlet servlet_=null;
	
	Scribe(ClavardServlet servlet) {
		servlet_=servlet;
	}
	
	public String construireWelcomeMessage(String pseudoAffecte) {
		//on envoie la liste
        if (servlet_.listeCoToString()!=null) {
            return "tomcat:"+pseudoAffecte+":welcome"+servlet_.listeCoToString();
        } else {
            return "tomcat:"+pseudoAffecte+":welcome";
        }
	}
	
	public String construireNewUserMessage(String pseudo, String port, String address) {
		//il faut aussi récupérer le port TCP
		//on devrait rajouter une condition ici : si
		//on a pas reçu de welcome après x secondes, on renvoie un hello
		//si, toujours rien reçu, on passe dernierCo
		return pseudo+":newuser:"+address+":"+port;
	}
	
	public String construireTchaoMessage(String pseudo) {
		return pseudo+":tchao:0";
	}
	
	public String construireNewPseudoMessage(String previousPseudo, String newPseudo) {
		return previousPseudo+":newpseudo:"+newPseudo;
	    }
	
}
