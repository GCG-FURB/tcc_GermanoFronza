package env;

// Environment code for project JasonProject

import jason.asSyntax.*;
import jason.environment.*;
import java.util.logging.*;

public class Classe extends Environment {

    private Logger logger = Logger.getLogger("JasonProject."+Classe.class.getName());

    /** Called before the MAS execution with the args informed in .mas2j */
    @Override
    public void init(String[] args) {
        addPercept(Literal.parseLiteral("percept(demo)"));
    }

    @Override
    public boolean executeAction(String agName, Structure action) {
        logger.info("executing: "+action+", but not implemented!");
        return true;
    }

    /** Called before the end of MAS execution */
    @Override
    public void stop() {
        super.stop();
    }
}
