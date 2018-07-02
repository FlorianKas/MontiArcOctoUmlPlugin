package ajava.test1;
component Test1Modell (int i) {
  component Component1(5) component1;
  component Component2(5,7) component2;
  connect component1.p1 -> component2.p2;
}
