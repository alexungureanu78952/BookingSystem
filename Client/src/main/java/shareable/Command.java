package shareable;

import java.io.Serializable;

public interface Command extends Serializable {
    // Interfață marker pentru toate comenzile
    long serialVersionUID = 1L;
}
