package reader;

import java.io.File;

public class MainReader {

    private File file;

    public MainReader(String[] args) {

        if ( args == null || args.length <= 0 ) {
            throw new NullPointerException("No Pseudocode was given to interpret.");
        } else if ( !args[0].endsWith(".pseudo") ) {
            throw new IllegalArgumentException("File does not end in .pseudo therefore is not a pseudo file.");
        }

        this.file = new File(args[0]);
    }

    public File getFile() {
        return this.file;
    }

}
