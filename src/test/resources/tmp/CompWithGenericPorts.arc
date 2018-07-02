package tmp;

/**
 * Valid model.
 */
component CompWithGenericPorts<K extends Number, V extends Iterable, W>(int i) {

    port
        in K myKInput,
        in W myWInput,
        out V myVOutput;  

}