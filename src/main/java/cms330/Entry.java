package test;

public class Entry 
{
    private String act;
    private String scene;
    private String character;
    private String title;
    private String line;

    public String getAct() 
    {
        return act;
    }

    public void setAct(String act) 
    {
        this.act = act;
    }

    public String getScene() {
        return scene;
    }

    public void setScene(String scene) {
        this.scene = scene;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCharacter() {
        return character;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    private String word;


    public Entry(String title, String act, String scene, String character, String word, String line) 
    {
        this.title = title;
        this.act =act;
        this.scene = scene;
        this.character= character;
        this.word = word;
        this.line = line;
    }
    
    public String toString()
    {
        StringBuilder retVal = new StringBuilder();

        retVal.append(this.title);
        retVal.append("\n\n");

        if (this.act.equals("PROLOGUE"))
        {
            retVal.append("PROLOGUE");
            retVal.append("\n\n");
        }
        else
        {
            retVal.append("Act ");
            retVal.append(this.act);
            
            
            if (this.scene.length() > 0)
            {
                retVal.append(", Scene ");
                retVal.append(this.scene);			
                retVal.append("\n\n");
            }

            if (this.character.length() > 0)
            {
                retVal.append(this.character);
                retVal.append("\n\n");
            }
        }
        retVal.append(this.character.toUpperCase());
        retVal.append(" ");
        retVal.append(this.line);

        return retVal.toString();
    }

}
