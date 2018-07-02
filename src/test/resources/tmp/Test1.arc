package tmp;
component Test1 <T extends Number>{
  component Comp1 comp1;
  component Comp2 comp2;
  connect comp1.integer -> comp2.integer;
}
