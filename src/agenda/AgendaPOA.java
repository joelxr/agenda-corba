package agenda;


/**
* agenda/AgendaPOA.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from Agenda.idl
* Terça-feira, 14 de Outubro de 2014 09h18min34s BRT
*/

public abstract class AgendaPOA extends org.omg.PortableServer.Servant
 implements agenda.AgendaOperations, org.omg.CORBA.portable.InvokeHandler
{

  // Constructors

  private static java.util.Hashtable _methods = new java.util.Hashtable ();
  static
  {
    _methods.put ("addContact", new java.lang.Integer (0));
    _methods.put ("updateContact", new java.lang.Integer (1));
    _methods.put ("deleteContact", new java.lang.Integer (2));
    _methods.put ("getContacts", new java.lang.Integer (3));
    _methods.put ("getName", new java.lang.Integer (4));
    _methods.put ("start", new java.lang.Integer (5));
    _methods.put ("syncContact", new java.lang.Integer (6));
    _methods.put ("addNearbyAgenda", new java.lang.Integer (7));
  }

  public org.omg.CORBA.portable.OutputStream _invoke (String $method,
                                org.omg.CORBA.portable.InputStream in,
                                org.omg.CORBA.portable.ResponseHandler $rh)
  {
    org.omg.CORBA.portable.OutputStream out = null;
    java.lang.Integer __method = (java.lang.Integer)_methods.get ($method);
    if (__method == null)
      throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);

    switch (__method.intValue ())
    {
       case 0:  // agenda/Agenda/addContact
       {
         try {
           agenda.Contact contact = agenda.ContactHelper.read (in);
           this.addContact (contact);
           out = $rh.createReply();
         } catch (agenda.ContactAlreadyExists $ex) {
           out = $rh.createExceptionReply ();
           agenda.ContactAlreadyExistsHelper.write (out, $ex);
         }
         break;
       }

       case 1:  // agenda/Agenda/updateContact
       {
         agenda.Contact contact = agenda.ContactHelper.read (in);
         this.updateContact (contact);
         out = $rh.createReply();
         break;
       }

       case 2:  // agenda/Agenda/deleteContact
       {
         agenda.Contact contact = agenda.ContactHelper.read (in);
         this.deleteContact (contact);
         out = $rh.createReply();
         break;
       }

       case 3:  // agenda/Agenda/getContacts
       {
         agenda.Contact $result[] = null;
         $result = this.getContacts ();
         out = $rh.createReply();
         agenda.ContactsHelper.write (out, $result);
         break;
       }

       case 4:  // agenda/Agenda/getName
       {
         String $result = null;
         $result = this.getName ();
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }

       case 5:  // agenda/Agenda/start
       {
         this.start ();
         out = $rh.createReply();
         break;
       }

       case 6:  // agenda/Agenda/syncContact
       {
         agenda.Contact contact = agenda.ContactHelper.read (in);
         String agendas[] = agenda.AgendasHelper.read (in);
         String action = in.read_string ();
         this.syncContact (contact, agendas, action);
         out = $rh.createReply();
         break;
       }

       case 7:  // agenda/Agenda/addNearbyAgenda
       {
         String name = in.read_string ();
         this.addNearbyAgenda (name);
         out = $rh.createReply();
         break;
       }

       default:
         throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
    }

    return out;
  } // _invoke

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:agenda/Agenda:1.0"};

  public String[] _all_interfaces (org.omg.PortableServer.POA poa, byte[] objectId)
  {
    return (String[])__ids.clone ();
  }

  public Agenda _this() 
  {
    return AgendaHelper.narrow(
    super._this_object());
  }

  public Agenda _this(org.omg.CORBA.ORB orb) 
  {
    return AgendaHelper.narrow(
    super._this_object(orb));
  }


} // class AgendaPOA
