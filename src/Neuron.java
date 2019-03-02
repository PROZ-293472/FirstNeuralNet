import java.util.ArrayList;
import java.util.Vector;

/*INFO - neurony porozumiewają się przez swoje indexy (layerIndex = 1 oznacza, że
* wszystkie wagi o indeksie 1 z poprzedniej warstwy będą połączone z danym neuronem)
* TODO Sprawdzić czy tak serio jest
* */
public class Neuron {

    private Double value;
    public Vector<Double> outputWages = new Vector<>();
    public int layerIndex;
    public Double bias;


    public Neuron(int layerIndex) {
        this.layerIndex = layerIndex;
        this.bias = -5 + Math.random()*10; //5 to maksymalny początkowy bias a min to -5 (może trzeba będzie zrobić oddzielny konstruktora parametr dla większych sieci)
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

    public void setValue(Double val) {
        this.value = val;
    }

    public void updateValue(ArrayList<Neuron> prevLayer) {
        Double sum = 0.0;
        for (Neuron neuron: prevLayer) {
            sum += neuron.getValue() * neuron.outputWages.get(this.layerIndex);//TODO Check this
        }
        sum -= this.bias;
        this.value = sigmoid(sum);
    }

    private static Double sigmoid(Double x) {
        return 1 / (1 + Math.exp(-x));
    }
}
