import { BellRing } from 'lucide-react';

export default function AlertPreferencePanel() {
  return (
    <section className="panel">
      <h2><BellRing size={20} />Alert preferences</h2>
      <label className="checkbox"><input type="checkbox" defaultChecked />Package price drops of 10% or more</label>
      <label className="checkbox"><input type="checkbox" defaultChecked />Flight price drops of 15% or more</label>
      <label className="checkbox"><input type="checkbox" defaultChecked />Strong AI alternative deals</label>
    </section>
  );
}
