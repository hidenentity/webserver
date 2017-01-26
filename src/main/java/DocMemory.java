import java.util.concurrent.atomic.AtomicInteger;


class DocMemory {

    private static final AtomicInteger COUNTER = new AtomicInteger(1);

    private final int id;

    private String number;

    private String description;

    public int getId() {
        return id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DocMemory(String number, String description) {
        this.id = COUNTER.getAndIncrement();
        this.number = number;
        this.description = description;
    }

    public DocMemory() {
        this.id = COUNTER.getAndIncrement();
    }


}
