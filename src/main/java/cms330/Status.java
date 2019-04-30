package test;

/*** A state blob reporting the status of the server ***/

public class Status {
    int count;

    public Status(int count) {
        this.count = count;
    }

    /*** Classes returned via REST must have get methods for class members ***/

    public int getCount() {
        return this.count;
    }
}