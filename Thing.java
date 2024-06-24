import java.io.IOException;

public abstract class Thing {

    public static class Item {
        private int weight;
        private int quantity;

        public Item(int weight, int quantity) {
            this.weight = weight;
            this.quantity = quantity;
        }

        public int getWeight() {
            return weight;
        }

        public int getQuantity() {
            return quantity;
        }
    }

    public Thing() { }

    // . . . other stuff . . .

    public boolean writeFile(String file) {
        if (file == null) return false;
        try {
            Writer out = createWriter();
            return out.write(file) > 0;
        }
        catch (IOException e) {
            return false;
        }
        // . . . other stuff . . .
    }

    protected abstract Writer createWriter();

    public boolean accept(Item[] items, int limit)
    {
        if(items == null || limit < 0)
        {
            throw new IllegalArgumentException();
        }
        int total = 0;
        for (Item it : items)
        {
            if(it == null)
            {
                throw new IllegalArgumentException();
            }
            int item_weight = it.getWeight() * it.getQuantity();
            if (item_weight < 0)
            {
                throw new IllegalArgumentException();
            }
            if (total < Integer.MAX_VALUE - item_weight)
            {
                total += item_weight;
            }
            else
            {
                return false;
            }


        }
        return total <= limit;

    }
}

