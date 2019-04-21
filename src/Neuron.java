import java.util.ArrayList;
import java.util.Vector;

/*INFO - neurony porozumiewają się przez swoje indexy (layerIndex = 1 oznacza, że
* wszystkie wagi o indeksie 1 z poprzedniej warstwy będą połączone z danym neuronem)
* TODO Sprawdzić czy tak serio jest
* */
public class Neuron {

    private Double value;
    private Double rawValue;
    public Vector<Double> outputWages = new Vector<>();
    public int layerIndex;
    public Double bias;
    public Double error;


    public Neuron(int layerIndex) {
        this.layerIndex = layerIndex;
        this.bias = -5 + Math.random()*10; //Idk if this is an optimal range. TODO Maybe add this to the constructor sometime?
        this.error = 0.0;
    }

    public void initialize(ArrayList<Neuron> prevLayer, ArrayList<Neuron> nextLayer) {
        if(nextLayer!=null) {
            int numOfOutputs = nextLayer.size();
            for (int c = 0; c < numOfOutputs; c++) {
                this.outputWages.add(-1 + Math.random() * 2); //random numbers in range [-1,1)
            }
        }
        else this.outputWages = null;

        if (prevLayer!=null) this.updateValue(prevLayer);
        else this.value = 0.0;

    }

    public Double getValue() {
        return this.value;
    }

    public Double getRawValue(){
        return this.rawValue;
    }

    public void setValue(Double val) {
        this.value = val;
    }

    public void updateValue(ArrayList<Neuron> prevLayer) {
        Double sum = 0.0;
        for (Neuron neuron: prevLayer) {
            sum += neuron.getValue() * neuron.outputWages.get(this.layerIndex);//TODO Check this
        }
        sum -= this.bias;
        this.rawValue = sum;
        this.value = sigmoid(sum);
    }


    private static Double sigmoid(Double x) {
        return 1 / (1 + Math.exp(-x));
    }
}
