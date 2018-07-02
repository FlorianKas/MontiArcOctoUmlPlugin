package tmp;
component A {
  component V v;
  component D d;
  connect v.integer -> d.string;
}
