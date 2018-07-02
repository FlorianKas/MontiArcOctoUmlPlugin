package tmp;
component Modell <T extends Number>{
  component Comp2 comp2;
  component Comp1 comp1;
  connect comp1.integer -> comp2.integer;
}
