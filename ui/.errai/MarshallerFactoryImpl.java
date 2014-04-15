package org.jboss.errai.marshalling.client.api;

import java.util.AbstractMap;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.enterprise.context.Dependent;
import org.jboss.errai.marshalling.client.api.json.EJArray;
import org.jboss.errai.marshalling.client.api.json.EJObject;
import org.jboss.errai.marshalling.client.api.json.EJValue;
import org.jboss.errai.marshalling.client.marshallers.BigDecimalMarshaller;
import org.jboss.errai.marshalling.client.marshallers.BigIntegerMarshaller;
import org.jboss.errai.marshalling.client.marshallers.BooleanMarshaller;
import org.jboss.errai.marshalling.client.marshallers.ByteMarshaller;
import org.jboss.errai.marshalling.client.marshallers.CharacterMarshaller;
import org.jboss.errai.marshalling.client.marshallers.DateMarshaller;
import org.jboss.errai.marshalling.client.marshallers.DoubleMarshaller;
import org.jboss.errai.marshalling.client.marshallers.FloatMarshaller;
import org.jboss.errai.marshalling.client.marshallers.IntegerMarshaller;
import org.jboss.errai.marshalling.client.marshallers.LinkedHashSetMarshaller;
import org.jboss.errai.marshalling.client.marshallers.LinkedListMarshaller;
import org.jboss.errai.marshalling.client.marshallers.LinkedMapMarshaller;
import org.jboss.errai.marshalling.client.marshallers.ListMarshaller;
import org.jboss.errai.marshalling.client.marshallers.LongMarshaller;
import org.jboss.errai.marshalling.client.marshallers.MapMarshaller;
import org.jboss.errai.marshalling.client.marshallers.ObjectMarshaller;
import org.jboss.errai.marshalling.client.marshallers.PriorityQueueMarshaller;
import org.jboss.errai.marshalling.client.marshallers.QualifyingMarshallerWrapper;
import org.jboss.errai.marshalling.client.marshallers.QueueMarshaller;
import org.jboss.errai.marshalling.client.marshallers.SQLDateMarshaller;
import org.jboss.errai.marshalling.client.marshallers.SetMarshaller;
import org.jboss.errai.marshalling.client.marshallers.ShortMarshaller;
import org.jboss.errai.marshalling.client.marshallers.SortedMapMarshaller;
import org.jboss.errai.marshalling.client.marshallers.SortedSetMarshaller;
import org.jboss.errai.marshalling.client.marshallers.StringBufferMarshaller;
import org.jboss.errai.marshalling.client.marshallers.StringBuilderMarshaller;
import org.jboss.errai.marshalling.client.marshallers.StringMarshaller;
import org.jboss.errai.marshalling.client.marshallers.TimeMarshaller;
import org.jboss.errai.marshalling.client.marshallers.TimestampMarshaller;
import org.jboss.errai.ui.shared.api.Locale;
import org.overlord.rtgov.ui.client.model.BatchRetryResult;
import org.overlord.rtgov.ui.client.model.CallTraceBean;
import org.overlord.rtgov.ui.client.model.ComponentServiceBean;
import org.overlord.rtgov.ui.client.model.ComponentServiceSummaryBean;
import org.overlord.rtgov.ui.client.model.MessageBean;
import org.overlord.rtgov.ui.client.model.NotificationBean;
import org.overlord.rtgov.ui.client.model.NotificationType;
import org.overlord.rtgov.ui.client.model.QName;
import org.overlord.rtgov.ui.client.model.ReferenceBean;
import org.overlord.rtgov.ui.client.model.ReferenceSummaryBean;
import org.overlord.rtgov.ui.client.model.ServiceBean;
import org.overlord.rtgov.ui.client.model.ServiceResultSetBean;
import org.overlord.rtgov.ui.client.model.ServiceSummaryBean;
import org.overlord.rtgov.ui.client.model.ServicesFilterBean;
import org.overlord.rtgov.ui.client.model.SituationBean;
import org.overlord.rtgov.ui.client.model.SituationEventBean;
import org.overlord.rtgov.ui.client.model.SituationResultSetBean;
import org.overlord.rtgov.ui.client.model.SituationSummaryBean;
import org.overlord.rtgov.ui.client.model.SituationsFilterBean;
import org.overlord.rtgov.ui.client.model.TraceNodeBean;
import org.overlord.rtgov.ui.client.model.UiException;
@Dependent public class MarshallerFactoryImpl implements MarshallerFactory {
  private Map<String, Marshaller> marshallers = new HashMap<String, Marshaller>();
  private ObjectMarshaller java_lang_Object;
  private BooleanMarshaller java_lang_Boolean;
  private SortedSetMarshaller java_util_SortedSet;
  private SortedSetMarshaller java_util_TreeSet;
  private IntegerMarshaller java_lang_Integer;
  private TimestampMarshaller java_sql_Timestamp;
  private ListMarshaller java_util_List;
  private ListMarshaller java_util_AbstractList;
  private ListMarshaller java_util_ArrayList;
  private ListMarshaller java_util_Vector;
  private ListMarshaller java_util_Stack;
  private StringMarshaller java_lang_String;
  private PriorityQueueMarshaller java_util_PriorityQueue;
  private QualifyingMarshallerWrapper<SortedMap> java_util_SortedMap;
  private QualifyingMarshallerWrapper<TreeMap> java_util_TreeMap;
  private BigIntegerMarshaller java_math_BigInteger;
  private CharacterMarshaller java_lang_Character;
  private ShortMarshaller java_lang_Short;
  private ByteMarshaller java_lang_Byte;
  private TimeMarshaller java_sql_Time;
  private BigDecimalMarshaller java_math_BigDecimal;
  private DateMarshaller java_util_Date;
  private LinkedListMarshaller java_util_LinkedList;
  private LongMarshaller java_lang_Long;
  private QualifyingMarshallerWrapper<Map> java_util_Map;
  private QualifyingMarshallerWrapper<AbstractMap> java_util_AbstractMap;
  private QualifyingMarshallerWrapper<HashMap> java_util_HashMap;
  private StringBufferMarshaller java_lang_StringBuffer;
  private QueueMarshaller java_util_Queue;
  private QueueMarshaller java_util_AbstractQueue;
  private QualifyingMarshallerWrapper<LinkedHashMap> java_util_LinkedHashMap;
  private DoubleMarshaller java_lang_Double;
  private SQLDateMarshaller java_sql_Date;
  private SetMarshaller java_util_Set;
  private SetMarshaller java_util_AbstractSet;
  private SetMarshaller java_util_HashSet;
  private LinkedHashSetMarshaller java_util_LinkedHashSet;
  private StringBuilderMarshaller java_lang_StringBuilder;
  private FloatMarshaller java_lang_Float;
  private Marshaller<StackTraceElement> java_lang_StackTraceElement;
  private QualifyingMarshallerWrapper<StackTraceElement[]> arrayOf_java_lang_StackTraceElement_D1;
  private Marshaller<Throwable> java_lang_Throwable;
  private Marshaller<MessageBean> org_overlord_rtgov_ui_client_model_MessageBean;
  private Marshaller<QName> org_overlord_rtgov_ui_client_model_QName;
  private Marshaller<SituationSummaryBean> org_overlord_rtgov_ui_client_model_SituationSummaryBean;
  private Marshaller<SituationsFilterBean> org_overlord_rtgov_ui_client_model_SituationsFilterBean;
  private Marshaller<ComponentServiceBean> org_overlord_rtgov_ui_client_model_ComponentServiceBean;
  private Marshaller<ReferenceBean> org_overlord_rtgov_ui_client_model_ReferenceBean;
  private Marshaller<UiException> org_overlord_rtgov_ui_client_model_UiException;
  private Marshaller<NotificationBean> org_overlord_rtgov_ui_client_model_NotificationBean;
  private Marshaller<Locale> org_jboss_errai_ui_shared_api_Locale;
  private Marshaller<TraceNodeBean> org_overlord_rtgov_ui_client_model_TraceNodeBean;
  private Marshaller<ServiceBean> org_overlord_rtgov_ui_client_model_ServiceBean;
  private Marshaller<ServiceSummaryBean> org_overlord_rtgov_ui_client_model_ServiceSummaryBean;
  private Marshaller<CallTraceBean> org_overlord_rtgov_ui_client_model_CallTraceBean;
  private Marshaller<ServicesFilterBean> org_overlord_rtgov_ui_client_model_ServicesFilterBean;
  private Marshaller<SituationEventBean> org_overlord_rtgov_ui_client_model_SituationEventBean;
  private Marshaller<SituationResultSetBean> org_overlord_rtgov_ui_client_model_SituationResultSetBean;
  private Marshaller<ComponentServiceSummaryBean> org_overlord_rtgov_ui_client_model_ComponentServiceSummaryBean;
  private Marshaller<BatchRetryResult> org_overlord_rtgov_ui_client_model_BatchRetryResult;
  private Marshaller<ReferenceSummaryBean> org_overlord_rtgov_ui_client_model_ReferenceSummaryBean;
  private Marshaller<ServiceResultSetBean> org_overlord_rtgov_ui_client_model_ServiceResultSetBean;
  private Marshaller<SituationBean> org_overlord_rtgov_ui_client_model_SituationBean;
  private Marshaller<NotificationType> org_overlord_rtgov_ui_client_model_NotificationType;
  public MarshallerFactoryImpl() {
    java_lang_Object = new ObjectMarshaller();
    marshallers.put("java.lang.Object", java_lang_Object);
    java_lang_Boolean = new BooleanMarshaller();
    marshallers.put("java.lang.Boolean", java_lang_Boolean);
    java_util_SortedSet = new SortedSetMarshaller();
    marshallers.put("java.util.SortedSet", java_util_SortedSet);
    marshallers.put("java.util.TreeSet", java_util_SortedSet);
    marshallers.put("java.util.Collections$SynchronizedSortedSet", java_util_SortedSet);
    marshallers.put("java.util.Collections$UnmodifiableSortedSet", java_util_SortedSet);
    java_util_TreeSet = new SortedSetMarshaller();
    marshallers.put("java.util.TreeSet", java_util_TreeSet);
    java_lang_Integer = new IntegerMarshaller();
    marshallers.put("java.lang.Integer", java_lang_Integer);
    java_sql_Timestamp = new TimestampMarshaller();
    marshallers.put("java.sql.Timestamp", java_sql_Timestamp);
    java_util_List = new ListMarshaller();
    marshallers.put("java.util.List", java_util_List);
    marshallers.put("java.util.AbstractList", java_util_List);
    marshallers.put("java.util.ArrayList", java_util_List);
    marshallers.put("java.util.Vector", java_util_List);
    marshallers.put("java.util.Stack", java_util_List);
    marshallers.put("java.util.Collections$SynchronizedRandomAccessList", java_util_List);
    marshallers.put("java.util.Collections$SingletonList", java_util_List);
    marshallers.put("java.util.Collections$SynchronizedList", java_util_List);
    marshallers.put("java.util.Collections$UnmodifiableRandomAccessList", java_util_List);
    marshallers.put("java.util.Collections$UnmodifiableList", java_util_List);
    marshallers.put("java.util.Collections$EmptyList", java_util_List);
    marshallers.put("java.util.Arrays$ArrayList", java_util_List);
    java_util_AbstractList = new ListMarshaller();
    marshallers.put("java.util.AbstractList", java_util_AbstractList);
    java_util_ArrayList = new ListMarshaller();
    marshallers.put("java.util.ArrayList", java_util_ArrayList);
    java_util_Vector = new ListMarshaller();
    marshallers.put("java.util.Vector", java_util_Vector);
    java_util_Stack = new ListMarshaller();
    marshallers.put("java.util.Stack", java_util_Stack);
    java_lang_String = new StringMarshaller();
    marshallers.put("java.lang.String", java_lang_String);
    java_util_PriorityQueue = new PriorityQueueMarshaller();
    marshallers.put("java.util.PriorityQueue", java_util_PriorityQueue);
    java_util_SortedMap = new QualifyingMarshallerWrapper(new SortedMapMarshaller(), SortedMapMarshaller.class);
    marshallers.put("java.util.SortedMap", java_util_SortedMap);
    marshallers.put("java.util.TreeMap", java_util_SortedMap);
    marshallers.put("java.util.Collections$UnmodifiableSortedMap", java_util_SortedMap);
    marshallers.put("java.util.Collections$SynchronizedSortedMap", java_util_SortedMap);
    java_util_TreeMap = new QualifyingMarshallerWrapper(new SortedMapMarshaller(), SortedMapMarshaller.class);
    marshallers.put("java.util.TreeMap", java_util_TreeMap);
    java_math_BigInteger = new BigIntegerMarshaller();
    marshallers.put("java.math.BigInteger", java_math_BigInteger);
    java_lang_Character = new CharacterMarshaller();
    marshallers.put("java.lang.Character", java_lang_Character);
    java_lang_Short = new ShortMarshaller();
    marshallers.put("java.lang.Short", java_lang_Short);
    java_lang_Byte = new ByteMarshaller();
    marshallers.put("java.lang.Byte", java_lang_Byte);
    java_sql_Time = new TimeMarshaller();
    marshallers.put("java.sql.Time", java_sql_Time);
    java_math_BigDecimal = new BigDecimalMarshaller();
    marshallers.put("java.math.BigDecimal", java_math_BigDecimal);
    java_util_Date = new DateMarshaller();
    marshallers.put("java.util.Date", java_util_Date);
    java_util_LinkedList = new LinkedListMarshaller();
    marshallers.put("java.util.LinkedList", java_util_LinkedList);
    java_lang_Long = new LongMarshaller();
    marshallers.put("java.lang.Long", java_lang_Long);
    java_util_Map = new QualifyingMarshallerWrapper(new MapMarshaller(), MapMarshaller.class);
    marshallers.put("java.util.Map", java_util_Map);
    marshallers.put("java.util.AbstractMap", java_util_Map);
    marshallers.put("java.util.HashMap", java_util_Map);
    marshallers.put("java.util.Collections$UnmodifiableMap", java_util_Map);
    marshallers.put("java.util.Collections$SingletonMap", java_util_Map);
    marshallers.put("java.util.Collections$EmptyMap", java_util_Map);
    marshallers.put("java.util.Collections$SynchronizedMap", java_util_Map);
    java_util_AbstractMap = new QualifyingMarshallerWrapper(new MapMarshaller(), MapMarshaller.class);
    marshallers.put("java.util.AbstractMap", java_util_AbstractMap);
    java_util_HashMap = new QualifyingMarshallerWrapper(new MapMarshaller(), MapMarshaller.class);
    marshallers.put("java.util.HashMap", java_util_HashMap);
    java_lang_StringBuffer = new StringBufferMarshaller();
    marshallers.put("java.lang.StringBuffer", java_lang_StringBuffer);
    java_util_Queue = new QueueMarshaller();
    marshallers.put("java.util.Queue", java_util_Queue);
    marshallers.put("java.util.AbstractQueue", java_util_Queue);
    java_util_AbstractQueue = new QueueMarshaller();
    marshallers.put("java.util.AbstractQueue", java_util_AbstractQueue);
    java_util_LinkedHashMap = new QualifyingMarshallerWrapper(new LinkedMapMarshaller(), LinkedMapMarshaller.class);
    marshallers.put("java.util.LinkedHashMap", java_util_LinkedHashMap);
    java_lang_Double = new DoubleMarshaller();
    marshallers.put("java.lang.Double", java_lang_Double);
    java_sql_Date = new SQLDateMarshaller();
    marshallers.put("java.sql.Date", java_sql_Date);
    java_util_Set = new SetMarshaller();
    marshallers.put("java.util.Set", java_util_Set);
    marshallers.put("java.util.AbstractSet", java_util_Set);
    marshallers.put("java.util.HashSet", java_util_Set);
    marshallers.put("java.util.Collections$SynchronizedSet", java_util_Set);
    marshallers.put("java.util.Collections$SingletonSet", java_util_Set);
    marshallers.put("java.util.Collections$UnmodifiableSet", java_util_Set);
    marshallers.put("java.util.Collections$EmptySet", java_util_Set);
    java_util_AbstractSet = new SetMarshaller();
    marshallers.put("java.util.AbstractSet", java_util_AbstractSet);
    java_util_HashSet = new SetMarshaller();
    marshallers.put("java.util.HashSet", java_util_HashSet);
    java_util_LinkedHashSet = new LinkedHashSetMarshaller();
    marshallers.put("java.util.LinkedHashSet", java_util_LinkedHashSet);
    java_lang_StringBuilder = new StringBuilderMarshaller();
    marshallers.put("java.lang.StringBuilder", java_lang_StringBuilder);
    java_lang_Float = new FloatMarshaller();
    marshallers.put("java.lang.Float", java_lang_Float);
    java_lang_StackTraceElement = new Marshaller<StackTraceElement>() {
      private StackTraceElement[] EMPTY_ARRAY = new StackTraceElement[0];
      public StackTraceElement[] getEmptyArray() {
        return EMPTY_ARRAY;
      }
      public StackTraceElement demarshall(EJValue a0, MarshallingSession a1) {
        EJObject obj = a0.isObject();
        if (obj == null) {
          return null;
        }
        String objId = obj.get("^ObjectID").isString().stringValue();
        if (a1.hasObject(objId)) {
          return a1.getObject(StackTraceElement.class, objId);
        }
        final String c0 = java_lang_String.demarshall(obj.get("declaringClass"), a1);
        final String c1 = java_lang_String.demarshall(obj.get("methodName"), a1);
        final String c2 = java_lang_String.demarshall(obj.get("fileName"), a1);
        final Integer c3 = java_lang_Integer.demarshall(obj.get("lineNumber"), a1);
        StackTraceElement entity = new StackTraceElement(c0, c1, c2, c3);
        a1.recordObject(objId, entity);
        return entity;
      }
      public String marshall(StackTraceElement a0, MarshallingSession a1) {
        if (a0 == null) {
          return "null";
        }
        final boolean ref = a1.hasObject(a0);
        final StringBuilder json = new StringBuilder("{\"^EncodedType\":\"java.lang.StackTraceElement\",\"^ObjectID\"");
        json.append(":\"").append(a1.getObject(a0)).append("\"");
        if (ref) {
          return json.append("}").toString();
        }
        return json.append(",").append("\"fileName\":").append(java_lang_String.marshall(a0.getFileName(), a1)).append(",").append("\"methodName\":").append(java_lang_String.marshall(a0.getMethodName(), a1)).append(",").append("\"lineNumber\":").append(java_lang_Integer.marshall(a0.getLineNumber(), a1)).append(",").append("\"declaringClass\":").append(java_lang_String.marshall(a0.getClassName(), a1)).append("}").toString();
      }
    };
    marshallers.put("java.lang.StackTraceElement", java_lang_StackTraceElement);
    arrayOf_java_lang_StackTraceElement_D1 = new QualifyingMarshallerWrapper(new Marshaller<StackTraceElement[]>() {
      public java.lang.StackTraceElement[][] getEmptyArray() {
        return null;
      }

      private StackTraceElement[] _demarshall1(EJArray a0, MarshallingSession a1) {
        StackTraceElement[] newArray = new StackTraceElement[a0.size()];
        for (int i = 0; i < newArray.length; i++) {
          newArray[i] = java_lang_StackTraceElement.demarshall(a0.get(i), a1);
        }
        return newArray;
      }

      private String _marshall1(StackTraceElement[] a0, MarshallingSession a1) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < a0.length; i++) {
          if (i > 0) {
            sb.append(",");
          }
          sb.append(java_lang_Object.marshall(a0[i], a1));
        }
        return sb.append("]").toString();
      }
      public StackTraceElement[] demarshall(EJValue a0, MarshallingSession a1) {
        if (a0 == null) {
          return null;
        } else {
          return this._demarshall1(a0.isArray(), a1);
        }
      }
      public String marshall(StackTraceElement[] a0, MarshallingSession a1) {
        if (a0 == null) {
          return null;
        } else {
          return this._marshall1(a0, a1);
        }
      }
    }, StackTraceElement[].class);
    marshallers.put("[Ljava.lang.StackTraceElement;", arrayOf_java_lang_StackTraceElement_D1);
    java_lang_Throwable = new Marshaller<Throwable>() {
      private Throwable[] EMPTY_ARRAY = new Throwable[0];
      public Throwable[] getEmptyArray() {
        return EMPTY_ARRAY;
      }
      public Throwable demarshall(EJValue a0, MarshallingSession a1) {
        EJObject obj = a0.isObject();
        if (obj == null) {
          return null;
        }
        String objId = obj.get("^ObjectID").isString().stringValue();
        if (a1.hasObject(objId)) {
          return a1.getObject(Throwable.class, objId);
        }
        final String c0 = java_lang_String.demarshall(obj.get("message"), a1);
        Throwable entity = new Throwable(c0);
        a1.recordObject(objId, entity);
        if ((obj.containsKey("cause")) && (!obj.get("cause").isNull())) {
          entity.initCause(java_lang_Throwable.demarshall(obj.get("cause"), a1));
        }
        if ((obj.containsKey("stackTrace")) && (!obj.get("stackTrace").isNull())) {
          entity.setStackTrace((StackTraceElement[]) arrayOf_java_lang_StackTraceElement_D1.demarshall(obj.get("stackTrace"), a1));
        }
        return entity;
      }
      public String marshall(Throwable a0, MarshallingSession a1) {
        if (a0 == null) {
          return "null";
        }
        final boolean ref = a1.hasObject(a0);
        final StringBuilder json = new StringBuilder("{\"^EncodedType\":\"java.lang.Throwable\",\"^ObjectID\"");
        json.append(":\"").append(a1.getObject(a0)).append("\"");
        if (ref) {
          return json.append("}").toString();
        }
        return json.append(",").append("\"stackTrace\":").append(arrayOf_java_lang_StackTraceElement_D1.marshall(a0.getStackTrace(), a1)).append(",").append("\"message\":").append(java_lang_String.marshall(a0.getMessage(), a1)).append(",").append("\"cause\":").append(java_lang_Throwable.marshall(a0.getCause(), a1)).append("}").toString();
      }
    };
    marshallers.put("java.lang.Throwable", java_lang_Throwable);
    org_overlord_rtgov_ui_client_model_MessageBean = new Marshaller<MessageBean>() {
      private MessageBean[] EMPTY_ARRAY = new MessageBean[0];
      public MessageBean[] getEmptyArray() {
        return EMPTY_ARRAY;
      }
      public MessageBean demarshall(EJValue a0, MarshallingSession a1) {
        EJObject obj = a0.isObject();
        if (obj == null) {
          return null;
        }
        String objId = obj.get("^ObjectID").isString().stringValue();
        if (a1.hasObject(objId)) {
          return a1.getObject(MessageBean.class, objId);
        }
        MessageBean entity = new MessageBean();
        a1.recordObject(objId, entity);
        if ((obj.containsKey("_content")) && (!obj.get("_content").isNull())) {
          _1193124396__1195259493__content(entity, java_lang_String.demarshall(obj.get("_content"), a1));
        }
        return entity;
      }
      public String marshall(MessageBean a0, MarshallingSession a1) {
        if (a0 == null) {
          return "null";
        }
        final boolean ref = a1.hasObject(a0);
        final StringBuilder json = new StringBuilder("{\"^EncodedType\":\"org.overlord.rtgov.ui.client.model.MessageBean\",\"^ObjectID\"");
        json.append(":\"").append(a1.getObject(a0)).append("\"");
        if (ref) {
          return json.append("}").toString();
        }
        return json.append(",").append("\"_content\":").append(java_lang_String.marshall(_1193124396__1195259493__content(a0), a1)).append("}").toString();
      }
    };
    marshallers.put("org.overlord.rtgov.ui.client.model.MessageBean", org_overlord_rtgov_ui_client_model_MessageBean);
    org_overlord_rtgov_ui_client_model_QName = new Marshaller<QName>() {
      private QName[] EMPTY_ARRAY = new QName[0];
      public QName[] getEmptyArray() {
        return EMPTY_ARRAY;
      }
      public QName demarshall(EJValue a0, MarshallingSession a1) {
        EJObject obj = a0.isObject();
        if (obj == null) {
          return null;
        }
        String objId = obj.get("^ObjectID").isString().stringValue();
        if (a1.hasObject(objId)) {
          return a1.getObject(QName.class, objId);
        }
        QName entity = new QName();
        a1.recordObject(objId, entity);
        if ((obj.containsKey("namespaceURI")) && (!obj.get("namespaceURI").isNull())) {
          entity.setNamespaceURI(java_lang_String.demarshall(obj.get("namespaceURI"), a1));
        }
        if ((obj.containsKey("localPart")) && (!obj.get("localPart").isNull())) {
          entity.setLocalPart(java_lang_String.demarshall(obj.get("localPart"), a1));
        }
        return entity;
      }
      public String marshall(QName a0, MarshallingSession a1) {
        if (a0 == null) {
          return "null";
        }
        final boolean ref = a1.hasObject(a0);
        final StringBuilder json = new StringBuilder("{\"^EncodedType\":\"org.overlord.rtgov.ui.client.model.QName\",\"^ObjectID\"");
        json.append(":\"").append(a1.getObject(a0)).append("\"");
        if (ref) {
          return json.append("}").toString();
        }
        return json.append(",").append("\"namespaceURI\":").append(java_lang_String.marshall(a0.getNamespaceURI(), a1)).append(",").append("\"localPart\":").append(java_lang_String.marshall(a0.getLocalPart(), a1)).append("}").toString();
      }
    };
    marshallers.put("org.overlord.rtgov.ui.client.model.QName", org_overlord_rtgov_ui_client_model_QName);
    org_overlord_rtgov_ui_client_model_SituationSummaryBean = new Marshaller<SituationSummaryBean>() {
      private SituationSummaryBean[] EMPTY_ARRAY = new SituationSummaryBean[0];
      public SituationSummaryBean[] getEmptyArray() {
        return EMPTY_ARRAY;
      }
      public SituationSummaryBean demarshall(EJValue a0, MarshallingSession a1) {
        EJObject obj = a0.isObject();
        if (obj == null) {
          return null;
        }
        String objId = obj.get("^ObjectID").isString().stringValue();
        if (a1.hasObject(objId)) {
          return a1.getObject(SituationSummaryBean.class, objId);
        }
        SituationSummaryBean entity = new SituationSummaryBean();
        a1.recordObject(objId, entity);
        if ((obj.containsKey("situationId")) && (!obj.get("situationId").isNull())) {
          entity.setSituationId(java_lang_String.demarshall(obj.get("situationId"), a1));
        }
        if ((obj.containsKey("severity")) && (!obj.get("severity").isNull())) {
          entity.setSeverity(java_lang_String.demarshall(obj.get("severity"), a1));
        }
        if ((obj.containsKey("type")) && (!obj.get("type").isNull())) {
          entity.setType(java_lang_String.demarshall(obj.get("type"), a1));
        }
        if ((obj.containsKey("subject")) && (!obj.get("subject").isNull())) {
          entity.setSubject(java_lang_String.demarshall(obj.get("subject"), a1));
        }
        if ((obj.containsKey("timestamp")) && (!obj.get("timestamp").isNull())) {
          entity.setTimestamp(java_util_Date.demarshall(obj.get("timestamp"), a1));
        }
        if ((obj.containsKey("description")) && (!obj.get("description").isNull())) {
          entity.setDescription(java_lang_String.demarshall(obj.get("description"), a1));
        }
        if ((obj.containsKey("properties")) && (!obj.get("properties").isNull())) {
          a1.setAssumedMapKeyType("java.lang.String");
          a1.setAssumedMapValueType("java.lang.String");
          entity.setProperties((Map) java_util_Map.demarshall(obj.get("properties"), a1));
          a1.resetAssumedTypes();
        }
        return entity;
      }
      public String marshall(SituationSummaryBean a0, MarshallingSession a1) {
        if (a0 == null) {
          return "null";
        }
        final boolean ref = a1.hasObject(a0);
        final StringBuilder json = new StringBuilder("{\"^EncodedType\":\"org.overlord.rtgov.ui.client.model.SituationSummaryBean\",\"^ObjectID\"");
        json.append(":\"").append(a1.getObject(a0)).append("\"");
        if (ref) {
          return json.append("}").toString();
        }
        return json.append(",").append("\"situationId\":").append(java_lang_String.marshall(a0.getSituationId(), a1)).append(",").append("\"severity\":").append(java_lang_String.marshall(a0.getSeverity(), a1)).append(",").append("\"type\":").append(java_lang_String.marshall(a0.getType(), a1)).append(",").append("\"subject\":").append(java_lang_String.marshall(a0.getSubject(), a1)).append(",").append("\"timestamp\":").append(java_util_Date.marshall(a0.getTimestamp(), a1)).append(",").append("\"description\":").append(java_lang_String.marshall(a0.getDescription(), a1)).append(",").append("\"properties\":").append(java_util_Map.marshall(a0.getProperties(), a1)).append("}").toString();
      }
    };
    marshallers.put("org.overlord.rtgov.ui.client.model.SituationSummaryBean", org_overlord_rtgov_ui_client_model_SituationSummaryBean);
    org_overlord_rtgov_ui_client_model_SituationsFilterBean = new Marshaller<SituationsFilterBean>() {
      private SituationsFilterBean[] EMPTY_ARRAY = new SituationsFilterBean[0];
      public SituationsFilterBean[] getEmptyArray() {
        return EMPTY_ARRAY;
      }
      public SituationsFilterBean demarshall(EJValue a0, MarshallingSession a1) {
        EJObject obj = a0.isObject();
        if (obj == null) {
          return null;
        }
        String objId = obj.get("^ObjectID").isString().stringValue();
        if (a1.hasObject(objId)) {
          return a1.getObject(SituationsFilterBean.class, objId);
        }
        SituationsFilterBean entity = new SituationsFilterBean();
        a1.recordObject(objId, entity);
        if ((obj.containsKey("severity")) && (!obj.get("severity").isNull())) {
          entity.setSeverity(java_lang_String.demarshall(obj.get("severity"), a1));
        }
        if ((obj.containsKey("type")) && (!obj.get("type").isNull())) {
          entity.setType(java_lang_String.demarshall(obj.get("type"), a1));
        }
        if ((obj.containsKey("resolutionState")) && (!obj.get("resolutionState").isNull())) {
          entity.setResolutionState(java_lang_String.demarshall(obj.get("resolutionState"), a1));
        }
        if ((obj.containsKey("timestampFrom")) && (!obj.get("timestampFrom").isNull())) {
          entity.setTimestampFrom(java_util_Date.demarshall(obj.get("timestampFrom"), a1));
        }
        if ((obj.containsKey("timestampTo")) && (!obj.get("timestampTo").isNull())) {
          entity.setTimestampTo(java_util_Date.demarshall(obj.get("timestampTo"), a1));
        }
        if ((obj.containsKey("description")) && (!obj.get("description").isNull())) {
          entity.setDescription(java_lang_String.demarshall(obj.get("description"), a1));
        }
        if ((obj.containsKey("subject")) && (!obj.get("subject").isNull())) {
          entity.setSubject(java_lang_String.demarshall(obj.get("subject"), a1));
        }
        if ((obj.containsKey("host")) && (!obj.get("host").isNull())) {
          entity.setHost(java_lang_String.demarshall(obj.get("host"), a1));
        }
        return entity;
      }
      public String marshall(SituationsFilterBean a0, MarshallingSession a1) {
        if (a0 == null) {
          return "null";
        }
        final boolean ref = a1.hasObject(a0);
        final StringBuilder json = new StringBuilder("{\"^EncodedType\":\"org.overlord.rtgov.ui.client.model.SituationsFilterBean\",\"^ObjectID\"");
        json.append(":\"").append(a1.getObject(a0)).append("\"");
        if (ref) {
          return json.append("}").toString();
        }
        return json.append(",").append("\"severity\":").append(java_lang_String.marshall(a0.getSeverity(), a1)).append(",").append("\"type\":").append(java_lang_String.marshall(a0.getType(), a1)).append(",").append("\"resolutionState\":").append(java_lang_String.marshall(a0.getResolutionState(), a1)).append(",").append("\"timestampFrom\":").append(java_util_Date.marshall(a0.getTimestampFrom(), a1)).append(",").append("\"timestampTo\":").append(java_util_Date.marshall(a0.getTimestampTo(), a1)).append(",").append("\"description\":").append(java_lang_String.marshall(a0.getDescription(), a1)).append(",").append("\"subject\":").append(java_lang_String.marshall(a0.getSubject(), a1)).append(",").append("\"host\":").append(java_lang_String.marshall(a0.getHost(), a1)).append("}").toString();
      }
    };
    marshallers.put("org.overlord.rtgov.ui.client.model.SituationsFilterBean", org_overlord_rtgov_ui_client_model_SituationsFilterBean);
    org_overlord_rtgov_ui_client_model_ComponentServiceBean = new Marshaller<ComponentServiceBean>() {
      private ComponentServiceBean[] EMPTY_ARRAY = new ComponentServiceBean[0];
      public ComponentServiceBean[] getEmptyArray() {
        return EMPTY_ARRAY;
      }
      public ComponentServiceBean demarshall(EJValue a0, MarshallingSession a1) {
        EJObject obj = a0.isObject();
        if (obj == null) {
          return null;
        }
        String objId = obj.get("^ObjectID").isString().stringValue();
        if (a1.hasObject(objId)) {
          return a1.getObject(ComponentServiceBean.class, objId);
        }
        ComponentServiceBean entity = new ComponentServiceBean();
        a1.recordObject(objId, entity);
        if ((obj.containsKey("serviceId")) && (!obj.get("serviceId").isNull())) {
          entity.setServiceId(java_lang_String.demarshall(obj.get("serviceId"), a1));
        }
        if ((obj.containsKey("name")) && (!obj.get("name").isNull())) {
          entity.setName(org_overlord_rtgov_ui_client_model_QName.demarshall(obj.get("name"), a1));
        }
        if ((obj.containsKey("application")) && (!obj.get("application").isNull())) {
          entity.setApplication(org_overlord_rtgov_ui_client_model_QName.demarshall(obj.get("application"), a1));
        }
        if ((obj.containsKey("serviceInterface")) && (!obj.get("serviceInterface").isNull())) {
          entity.setServiceInterface(java_lang_String.demarshall(obj.get("serviceInterface"), a1));
        }
        if ((obj.containsKey("serviceImplementation")) && (!obj.get("serviceImplementation").isNull())) {
          entity.setServiceImplementation(java_lang_String.demarshall(obj.get("serviceImplementation"), a1));
        }
        return entity;
      }
      public String marshall(ComponentServiceBean a0, MarshallingSession a1) {
        if (a0 == null) {
          return "null";
        }
        final boolean ref = a1.hasObject(a0);
        final StringBuilder json = new StringBuilder("{\"^EncodedType\":\"org.overlord.rtgov.ui.client.model.ComponentServiceBean\",\"^ObjectID\"");
        json.append(":\"").append(a1.getObject(a0)).append("\"");
        if (ref) {
          return json.append("}").toString();
        }
        return json.append(",").append("\"serviceId\":").append(java_lang_String.marshall(a0.getServiceId(), a1)).append(",").append("\"name\":").append(org_overlord_rtgov_ui_client_model_QName.marshall(a0.getName(), a1)).append(",").append("\"application\":").append(org_overlord_rtgov_ui_client_model_QName.marshall(a0.getApplication(), a1)).append(",").append("\"serviceInterface\":").append(java_lang_String.marshall(a0.getServiceInterface(), a1)).append(",").append("\"serviceImplementation\":").append(java_lang_String.marshall(a0.getServiceImplementation(), a1)).append("}").toString();
      }
    };
    marshallers.put("org.overlord.rtgov.ui.client.model.ComponentServiceBean", org_overlord_rtgov_ui_client_model_ComponentServiceBean);
    org_overlord_rtgov_ui_client_model_ReferenceBean = new Marshaller<ReferenceBean>() {
      private ReferenceBean[] EMPTY_ARRAY = new ReferenceBean[0];
      public ReferenceBean[] getEmptyArray() {
        return EMPTY_ARRAY;
      }
      public ReferenceBean demarshall(EJValue a0, MarshallingSession a1) {
        EJObject obj = a0.isObject();
        if (obj == null) {
          return null;
        }
        String objId = obj.get("^ObjectID").isString().stringValue();
        if (a1.hasObject(objId)) {
          return a1.getObject(ReferenceBean.class, objId);
        }
        ReferenceBean entity = new ReferenceBean();
        a1.recordObject(objId, entity);
        if ((obj.containsKey("referenceId")) && (!obj.get("referenceId").isNull())) {
          entity.setReferenceId(java_lang_String.demarshall(obj.get("referenceId"), a1));
        }
        if ((obj.containsKey("name")) && (!obj.get("name").isNull())) {
          entity.setName(org_overlord_rtgov_ui_client_model_QName.demarshall(obj.get("name"), a1));
        }
        if ((obj.containsKey("application")) && (!obj.get("application").isNull())) {
          entity.setApplication(org_overlord_rtgov_ui_client_model_QName.demarshall(obj.get("application"), a1));
        }
        if ((obj.containsKey("referenceInterface")) && (!obj.get("referenceInterface").isNull())) {
          entity.setReferenceInterface(java_lang_String.demarshall(obj.get("referenceInterface"), a1));
        }
        return entity;
      }
      public String marshall(ReferenceBean a0, MarshallingSession a1) {
        if (a0 == null) {
          return "null";
        }
        final boolean ref = a1.hasObject(a0);
        final StringBuilder json = new StringBuilder("{\"^EncodedType\":\"org.overlord.rtgov.ui.client.model.ReferenceBean\",\"^ObjectID\"");
        json.append(":\"").append(a1.getObject(a0)).append("\"");
        if (ref) {
          return json.append("}").toString();
        }
        return json.append(",").append("\"referenceId\":").append(java_lang_String.marshall(a0.getReferenceId(), a1)).append(",").append("\"name\":").append(org_overlord_rtgov_ui_client_model_QName.marshall(a0.getName(), a1)).append(",").append("\"application\":").append(org_overlord_rtgov_ui_client_model_QName.marshall(a0.getApplication(), a1)).append(",").append("\"referenceInterface\":").append(java_lang_String.marshall(a0.getReferenceInterface(), a1)).append("}").toString();
      }
    };
    marshallers.put("org.overlord.rtgov.ui.client.model.ReferenceBean", org_overlord_rtgov_ui_client_model_ReferenceBean);
    org_overlord_rtgov_ui_client_model_UiException = new Marshaller<UiException>() {
      private UiException[] EMPTY_ARRAY = new UiException[0];
      public UiException[] getEmptyArray() {
        return EMPTY_ARRAY;
      }
      public UiException demarshall(EJValue a0, MarshallingSession a1) {
        EJObject obj = a0.isObject();
        if (obj == null) {
          return null;
        }
        String objId = obj.get("^ObjectID").isString().stringValue();
        if (a1.hasObject(objId)) {
          return a1.getObject(UiException.class, objId);
        }
        final String c0 = java_lang_String.demarshall(obj.get("message"), a1);
        UiException entity = new UiException(c0);
        a1.recordObject(objId, entity);
        if ((obj.containsKey("detailMessage")) && (!obj.get("detailMessage").isNull())) {
          _1630335596__1195259493_detailMessage(entity, java_lang_String.demarshall(obj.get("detailMessage"), a1));
        }
        if ((obj.containsKey("cause")) && (!obj.get("cause").isNull())) {
          entity.initCause(java_lang_Throwable.demarshall(obj.get("cause"), a1));
        }
        if ((obj.containsKey("stackTrace")) && (!obj.get("stackTrace").isNull())) {
          entity.setStackTrace((StackTraceElement[]) arrayOf_java_lang_StackTraceElement_D1.demarshall(obj.get("stackTrace"), a1));
        }
        return entity;
      }
      public String marshall(UiException a0, MarshallingSession a1) {
        if (a0 == null) {
          return "null";
        }
        final boolean ref = a1.hasObject(a0);
        final StringBuilder json = new StringBuilder("{\"^EncodedType\":\"org.overlord.rtgov.ui.client.model.UiException\",\"^ObjectID\"");
        json.append(":\"").append(a1.getObject(a0)).append("\"");
        if (ref) {
          return json.append("}").toString();
        }
        return json.append(",").append("\"detailMessage\":").append(java_lang_String.marshall(_1630335596__1195259493_detailMessage(a0), a1)).append(",").append("\"stackTrace\":").append(arrayOf_java_lang_StackTraceElement_D1.marshall(a0.getStackTrace(), a1)).append(",").append("\"message\":").append(java_lang_String.marshall(a0.getMessage(), a1)).append(",").append("\"cause\":").append(java_lang_Throwable.marshall(a0.getCause(), a1)).append("}").toString();
      }
    };
    marshallers.put("org.overlord.rtgov.ui.client.model.UiException", org_overlord_rtgov_ui_client_model_UiException);
    org_overlord_rtgov_ui_client_model_NotificationBean = new Marshaller<NotificationBean>() {
      private NotificationBean[] EMPTY_ARRAY = new NotificationBean[0];
      public NotificationBean[] getEmptyArray() {
        return EMPTY_ARRAY;
      }
      public NotificationBean demarshall(EJValue a0, MarshallingSession a1) {
        EJObject obj = a0.isObject();
        if (obj == null) {
          return null;
        }
        String objId = obj.get("^ObjectID").isString().stringValue();
        if (a1.hasObject(objId)) {
          return a1.getObject(NotificationBean.class, objId);
        }
        NotificationBean entity = new NotificationBean();
        a1.recordObject(objId, entity);
        if ((obj.containsKey("uuid")) && (!obj.get("uuid").isNull())) {
          entity.setUuid(java_lang_String.demarshall(obj.get("uuid"), a1));
        }
        if ((obj.containsKey("type")) && (!obj.get("type").isNull())) {
          entity.setType(obj.get("type").isObject() != null ? Enum.valueOf(NotificationType.class, obj.get("type").isObject().get("^EnumStringValue").isString().stringValue()) : obj.get("type").isString() != null ? Enum.valueOf(NotificationType.class, obj.get("type").isString().stringValue()) : null);
        }
        if ((obj.containsKey("date")) && (!obj.get("date").isNull())) {
          _1968081542__65575278_date(entity, java_util_Date.demarshall(obj.get("date"), a1));
        }
        if ((obj.containsKey("title")) && (!obj.get("title").isNull())) {
          entity.setTitle(java_lang_String.demarshall(obj.get("title"), a1));
        }
        if ((obj.containsKey("message")) && (!obj.get("message").isNull())) {
          entity.setMessage(java_lang_String.demarshall(obj.get("message"), a1));
        }
        if ((obj.containsKey("exception")) && (!obj.get("exception").isNull())) {
          entity.setException(org_overlord_rtgov_ui_client_model_UiException.demarshall(obj.get("exception"), a1));
        }
        return entity;
      }
      public String marshall(NotificationBean a0, MarshallingSession a1) {
        if (a0 == null) {
          return "null";
        }
        final boolean ref = a1.hasObject(a0);
        final StringBuilder json = new StringBuilder("{\"^EncodedType\":\"org.overlord.rtgov.ui.client.model.NotificationBean\",\"^ObjectID\"");
        json.append(":\"").append(a1.getObject(a0)).append("\"");
        if (ref) {
          return json.append("}").toString();
        }
        return json.append(",").append("\"uuid\":").append(java_lang_String.marshall(a0.getUuid(), a1)).append(",").append("\"type\":").append(a0.getType() != null ? new StringBuilder(64).append("{\"^EncodedType\":\"org.overlord.rtgov.ui.client.model.NotificationType\",\"^EnumStringValue\":\"").append(a0.getType().name()).append("\"}") : "null").append(",").append("\"date\":").append(java_util_Date.marshall(a0.getDate(), a1)).append(",").append("\"title\":").append(java_lang_String.marshall(a0.getTitle(), a1)).append(",").append("\"message\":").append(java_lang_String.marshall(a0.getMessage(), a1)).append(",").append("\"exception\":").append(org_overlord_rtgov_ui_client_model_UiException.marshall(a0.getException(), a1)).append("}").toString();
      }
    };
    marshallers.put("org.overlord.rtgov.ui.client.model.NotificationBean", org_overlord_rtgov_ui_client_model_NotificationBean);
    org_jboss_errai_ui_shared_api_Locale = new Marshaller<Locale>() {
      private Locale[] EMPTY_ARRAY = new Locale[0];
      public Locale[] getEmptyArray() {
        return EMPTY_ARRAY;
      }
      public Locale demarshall(EJValue a0, MarshallingSession a1) {
        EJObject obj = a0.isObject();
        if (obj == null) {
          return null;
        }
        String objId = obj.get("^ObjectID").isString().stringValue();
        if (a1.hasObject(objId)) {
          return a1.getObject(Locale.class, objId);
        }
        final String c0 = java_lang_String.demarshall(obj.get("locale"), a1);
        final String c1 = java_lang_String.demarshall(obj.get("label"), a1);
        Locale entity = new Locale(c0, c1);
        a1.recordObject(objId, entity);
        return entity;
      }
      public String marshall(Locale a0, MarshallingSession a1) {
        if (a0 == null) {
          return "null";
        }
        final boolean ref = a1.hasObject(a0);
        final StringBuilder json = new StringBuilder("{\"^EncodedType\":\"org.jboss.errai.ui.shared.api.Locale\",\"^ObjectID\"");
        json.append(":\"").append(a1.getObject(a0)).append("\"");
        if (ref) {
          return json.append("}").toString();
        }
        return json.append(",").append("\"locale\":").append(java_lang_String.marshall(a0.getLocale(), a1)).append(",").append("\"label\":").append(java_lang_String.marshall(a0.getLabel(), a1)).append("}").toString();
      }
    };
    marshallers.put("org.jboss.errai.ui.shared.api.Locale", org_jboss_errai_ui_shared_api_Locale);
    org_overlord_rtgov_ui_client_model_TraceNodeBean = new Marshaller<TraceNodeBean>() {
      private TraceNodeBean[] EMPTY_ARRAY = new TraceNodeBean[0];
      public TraceNodeBean[] getEmptyArray() {
        return EMPTY_ARRAY;
      }
      public TraceNodeBean demarshall(EJValue a0, MarshallingSession a1) {
        EJObject obj = a0.isObject();
        if (obj == null) {
          return null;
        }
        String objId = obj.get("^ObjectID").isString().stringValue();
        if (a1.hasObject(objId)) {
          return a1.getObject(TraceNodeBean.class, objId);
        }
        TraceNodeBean entity = new TraceNodeBean();
        a1.recordObject(objId, entity);
        if ((obj.containsKey("type")) && (!obj.get("type").isNull())) {
          entity.setType(java_lang_String.demarshall(obj.get("type"), a1));
        }
        if ((obj.containsKey("iface")) && (!obj.get("iface").isNull())) {
          entity.setIface(java_lang_String.demarshall(obj.get("iface"), a1));
        }
        if ((obj.containsKey("operation")) && (!obj.get("operation").isNull())) {
          entity.setOperation(java_lang_String.demarshall(obj.get("operation"), a1));
        }
        if ((obj.containsKey("fault")) && (!obj.get("fault").isNull())) {
          entity.setFault(java_lang_String.demarshall(obj.get("fault"), a1));
        }
        if ((obj.containsKey("component")) && (!obj.get("component").isNull())) {
          entity.setComponent(java_lang_String.demarshall(obj.get("component"), a1));
        }
        if ((obj.containsKey("request")) && (!obj.get("request").isNull())) {
          entity.setRequest(java_lang_String.demarshall(obj.get("request"), a1));
        }
        if ((obj.containsKey("response")) && (!obj.get("response").isNull())) {
          entity.setResponse(java_lang_String.demarshall(obj.get("response"), a1));
        }
        if ((obj.containsKey("principal")) && (!obj.get("principal").isNull())) {
          entity.setPrincipal(java_lang_String.demarshall(obj.get("principal"), a1));
        }
        if ((obj.containsKey("requestLatency")) && (!obj.get("requestLatency").isNull())) {
          entity.setRequestLatency(java_lang_Long.demarshall(obj.get("requestLatency"), a1));
        }
        if ((obj.containsKey("responseLatency")) && (!obj.get("responseLatency").isNull())) {
          entity.setResponseLatency(java_lang_Long.demarshall(obj.get("responseLatency"), a1));
        }
        if ((obj.containsKey("duration")) && (!obj.get("duration").isNull())) {
          entity.setDuration(java_lang_Long.demarshall(obj.get("duration"), a1));
        }
        if ((obj.containsKey("percentage")) && (!obj.get("percentage").isNull())) {
          entity.setPercentage(java_lang_Integer.demarshall(obj.get("percentage"), a1));
        }
        if ((obj.containsKey("status")) && (!obj.get("status").isNull())) {
          entity.setStatus(java_lang_String.demarshall(obj.get("status"), a1));
        }
        if ((obj.containsKey("properties")) && (!obj.get("properties").isNull())) {
          a1.setAssumedMapKeyType("java.lang.String");
          a1.setAssumedMapValueType("java.lang.String");
          entity.setProperties((Map) java_util_Map.demarshall(obj.get("properties"), a1));
          a1.resetAssumedTypes();
        }
        if ((obj.containsKey("description")) && (!obj.get("description").isNull())) {
          entity.setDescription(java_lang_String.demarshall(obj.get("description"), a1));
        }
        if ((obj.containsKey("tasks")) && (!obj.get("tasks").isNull())) {
          a1.setAssumedElementType("org.overlord.rtgov.ui.client.model.TraceNodeBean");
          entity.setTasks(java_util_List.demarshall(obj.get("tasks"), a1));
        }
        return entity;
      }
      public String marshall(TraceNodeBean a0, MarshallingSession a1) {
        if (a0 == null) {
          return "null";
        }
        final boolean ref = a1.hasObject(a0);
        final StringBuilder json = new StringBuilder("{\"^EncodedType\":\"org.overlord.rtgov.ui.client.model.TraceNodeBean\",\"^ObjectID\"");
        json.append(":\"").append(a1.getObject(a0)).append("\"");
        if (ref) {
          return json.append("}").toString();
        }
        return json.append(",").append("\"type\":").append(java_lang_String.marshall(a0.getType(), a1)).append(",").append("\"iface\":").append(java_lang_String.marshall(a0.getIface(), a1)).append(",").append("\"operation\":").append(java_lang_String.marshall(a0.getOperation(), a1)).append(",").append("\"fault\":").append(java_lang_String.marshall(a0.getFault(), a1)).append(",").append("\"component\":").append(java_lang_String.marshall(a0.getComponent(), a1)).append(",").append("\"request\":").append(java_lang_String.marshall(a0.getRequest(), a1)).append(",").append("\"response\":").append(java_lang_String.marshall(a0.getResponse(), a1)).append(",").append("\"principal\":").append(java_lang_String.marshall(a0.getPrincipal(), a1)).append(",").append("\"requestLatency\":").append(java_lang_Long.marshall(a0.getRequestLatency(), a1)).append(",").append("\"responseLatency\":").append(java_lang_Long.marshall(a0.getResponseLatency(), a1)).append(",").append("\"duration\":").append(java_lang_Long.marshall(a0.getDuration(), a1)).append(",").append("\"percentage\":").append(java_lang_Integer.marshall(a0.getPercentage(), a1)).append(",").append("\"status\":").append(java_lang_String.marshall(a0.getStatus(), a1)).append(",").append("\"properties\":").append(java_util_Map.marshall(a0.getProperties(), a1)).append(",").append("\"description\":").append(java_lang_String.marshall(a0.getDescription(), a1)).append(",").append("\"tasks\":").append(java_util_List.marshall(a0.getTasks(), a1)).append("}").toString();
      }
    };
    marshallers.put("org.overlord.rtgov.ui.client.model.TraceNodeBean", org_overlord_rtgov_ui_client_model_TraceNodeBean);
    org_overlord_rtgov_ui_client_model_ServiceBean = new Marshaller<ServiceBean>() {
      private ServiceBean[] EMPTY_ARRAY = new ServiceBean[0];
      public ServiceBean[] getEmptyArray() {
        return EMPTY_ARRAY;
      }
      public ServiceBean demarshall(EJValue a0, MarshallingSession a1) {
        EJObject obj = a0.isObject();
        if (obj == null) {
          return null;
        }
        String objId = obj.get("^ObjectID").isString().stringValue();
        if (a1.hasObject(objId)) {
          return a1.getObject(ServiceBean.class, objId);
        }
        ServiceBean entity = new ServiceBean();
        a1.recordObject(objId, entity);
        if ((obj.containsKey("serviceId")) && (!obj.get("serviceId").isNull())) {
          entity.setServiceId(java_lang_String.demarshall(obj.get("serviceId"), a1));
        }
        if ((obj.containsKey("name")) && (!obj.get("name").isNull())) {
          entity.setName(org_overlord_rtgov_ui_client_model_QName.demarshall(obj.get("name"), a1));
        }
        if ((obj.containsKey("application")) && (!obj.get("application").isNull())) {
          entity.setApplication(org_overlord_rtgov_ui_client_model_QName.demarshall(obj.get("application"), a1));
        }
        if ((obj.containsKey("serviceInterface")) && (!obj.get("serviceInterface").isNull())) {
          entity.setServiceInterface(java_lang_String.demarshall(obj.get("serviceInterface"), a1));
        }
        if ((obj.containsKey("references")) && (!obj.get("references").isNull())) {
          a1.setAssumedElementType("org.overlord.rtgov.ui.client.model.ReferenceSummaryBean");
          entity.setReferences(java_util_List.demarshall(obj.get("references"), a1));
        }
        return entity;
      }
      public String marshall(ServiceBean a0, MarshallingSession a1) {
        if (a0 == null) {
          return "null";
        }
        final boolean ref = a1.hasObject(a0);
        final StringBuilder json = new StringBuilder("{\"^EncodedType\":\"org.overlord.rtgov.ui.client.model.ServiceBean\",\"^ObjectID\"");
        json.append(":\"").append(a1.getObject(a0)).append("\"");
        if (ref) {
          return json.append("}").toString();
        }
        return json.append(",").append("\"serviceId\":").append(java_lang_String.marshall(a0.getServiceId(), a1)).append(",").append("\"name\":").append(org_overlord_rtgov_ui_client_model_QName.marshall(a0.getName(), a1)).append(",").append("\"application\":").append(org_overlord_rtgov_ui_client_model_QName.marshall(a0.getApplication(), a1)).append(",").append("\"serviceInterface\":").append(java_lang_String.marshall(a0.getServiceInterface(), a1)).append(",").append("\"references\":").append(java_util_List.marshall(a0.getReferences(), a1)).append("}").toString();
      }
    };
    marshallers.put("org.overlord.rtgov.ui.client.model.ServiceBean", org_overlord_rtgov_ui_client_model_ServiceBean);
    org_overlord_rtgov_ui_client_model_ServiceSummaryBean = new Marshaller<ServiceSummaryBean>() {
      private ServiceSummaryBean[] EMPTY_ARRAY = new ServiceSummaryBean[0];
      public ServiceSummaryBean[] getEmptyArray() {
        return EMPTY_ARRAY;
      }
      public ServiceSummaryBean demarshall(EJValue a0, MarshallingSession a1) {
        EJObject obj = a0.isObject();
        if (obj == null) {
          return null;
        }
        String objId = obj.get("^ObjectID").isString().stringValue();
        if (a1.hasObject(objId)) {
          return a1.getObject(ServiceSummaryBean.class, objId);
        }
        ServiceSummaryBean entity = new ServiceSummaryBean();
        a1.recordObject(objId, entity);
        if ((obj.containsKey("serviceId")) && (!obj.get("serviceId").isNull())) {
          entity.setServiceId(java_lang_String.demarshall(obj.get("serviceId"), a1));
        }
        if ((obj.containsKey("name")) && (!obj.get("name").isNull())) {
          entity.setName(java_lang_String.demarshall(obj.get("name"), a1));
        }
        if ((obj.containsKey("application")) && (!obj.get("application").isNull())) {
          entity.setApplication(java_lang_String.demarshall(obj.get("application"), a1));
        }
        if ((obj.containsKey("iface")) && (!obj.get("iface").isNull())) {
          entity.setIface(java_lang_String.demarshall(obj.get("iface"), a1));
        }
        if ((obj.containsKey("bindings")) && (!obj.get("bindings").isNull())) {
          entity.setBindings(java_lang_String.demarshall(obj.get("bindings"), a1));
        }
        return entity;
      }
      public String marshall(ServiceSummaryBean a0, MarshallingSession a1) {
        if (a0 == null) {
          return "null";
        }
        final boolean ref = a1.hasObject(a0);
        final StringBuilder json = new StringBuilder("{\"^EncodedType\":\"org.overlord.rtgov.ui.client.model.ServiceSummaryBean\",\"^ObjectID\"");
        json.append(":\"").append(a1.getObject(a0)).append("\"");
        if (ref) {
          return json.append("}").toString();
        }
        return json.append(",").append("\"serviceId\":").append(java_lang_String.marshall(a0.getServiceId(), a1)).append(",").append("\"name\":").append(java_lang_String.marshall(a0.getName(), a1)).append(",").append("\"application\":").append(java_lang_String.marshall(a0.getApplication(), a1)).append(",").append("\"iface\":").append(java_lang_String.marshall(a0.getIface(), a1)).append(",").append("\"bindings\":").append(java_lang_String.marshall(a0.getBindings(), a1)).append("}").toString();
      }
    };
    marshallers.put("org.overlord.rtgov.ui.client.model.ServiceSummaryBean", org_overlord_rtgov_ui_client_model_ServiceSummaryBean);
    org_overlord_rtgov_ui_client_model_CallTraceBean = new Marshaller<CallTraceBean>() {
      private CallTraceBean[] EMPTY_ARRAY = new CallTraceBean[0];
      public CallTraceBean[] getEmptyArray() {
        return EMPTY_ARRAY;
      }
      public CallTraceBean demarshall(EJValue a0, MarshallingSession a1) {
        EJObject obj = a0.isObject();
        if (obj == null) {
          return null;
        }
        String objId = obj.get("^ObjectID").isString().stringValue();
        if (a1.hasObject(objId)) {
          return a1.getObject(CallTraceBean.class, objId);
        }
        CallTraceBean entity = new CallTraceBean();
        a1.recordObject(objId, entity);
        if ((obj.containsKey("tasks")) && (!obj.get("tasks").isNull())) {
          a1.setAssumedElementType("org.overlord.rtgov.ui.client.model.TraceNodeBean");
          entity.setTasks(java_util_List.demarshall(obj.get("tasks"), a1));
        }
        return entity;
      }
      public String marshall(CallTraceBean a0, MarshallingSession a1) {
        if (a0 == null) {
          return "null";
        }
        final boolean ref = a1.hasObject(a0);
        final StringBuilder json = new StringBuilder("{\"^EncodedType\":\"org.overlord.rtgov.ui.client.model.CallTraceBean\",\"^ObjectID\"");
        json.append(":\"").append(a1.getObject(a0)).append("\"");
        if (ref) {
          return json.append("}").toString();
        }
        return json.append(",").append("\"tasks\":").append(java_util_List.marshall(a0.getTasks(), a1)).append("}").toString();
      }
    };
    marshallers.put("org.overlord.rtgov.ui.client.model.CallTraceBean", org_overlord_rtgov_ui_client_model_CallTraceBean);
    org_overlord_rtgov_ui_client_model_ServicesFilterBean = new Marshaller<ServicesFilterBean>() {
      private ServicesFilterBean[] EMPTY_ARRAY = new ServicesFilterBean[0];
      public ServicesFilterBean[] getEmptyArray() {
        return EMPTY_ARRAY;
      }
      public ServicesFilterBean demarshall(EJValue a0, MarshallingSession a1) {
        EJObject obj = a0.isObject();
        if (obj == null) {
          return null;
        }
        String objId = obj.get("^ObjectID").isString().stringValue();
        if (a1.hasObject(objId)) {
          return a1.getObject(ServicesFilterBean.class, objId);
        }
        ServicesFilterBean entity = new ServicesFilterBean();
        a1.recordObject(objId, entity);
        if ((obj.containsKey("applicationName")) && (!obj.get("applicationName").isNull())) {
          entity.setApplicationName(java_lang_String.demarshall(obj.get("applicationName"), a1));
        }
        if ((obj.containsKey("serviceName")) && (!obj.get("serviceName").isNull())) {
          entity.setServiceName(java_lang_String.demarshall(obj.get("serviceName"), a1));
        }
        if ((obj.containsKey("processingState")) && (!obj.get("processingState").isNull())) {
          entity.setProcessingState(java_lang_String.demarshall(obj.get("processingState"), a1));
        }
        return entity;
      }
      public String marshall(ServicesFilterBean a0, MarshallingSession a1) {
        if (a0 == null) {
          return "null";
        }
        final boolean ref = a1.hasObject(a0);
        final StringBuilder json = new StringBuilder("{\"^EncodedType\":\"org.overlord.rtgov.ui.client.model.ServicesFilterBean\",\"^ObjectID\"");
        json.append(":\"").append(a1.getObject(a0)).append("\"");
        if (ref) {
          return json.append("}").toString();
        }
        return json.append(",").append("\"applicationName\":").append(java_lang_String.marshall(a0.getApplicationName(), a1)).append(",").append("\"serviceName\":").append(java_lang_String.marshall(a0.getServiceName(), a1)).append(",").append("\"processingState\":").append(java_lang_String.marshall(a0.getProcessingState(), a1)).append("}").toString();
      }
    };
    marshallers.put("org.overlord.rtgov.ui.client.model.ServicesFilterBean", org_overlord_rtgov_ui_client_model_ServicesFilterBean);
    org_overlord_rtgov_ui_client_model_SituationEventBean = new Marshaller<SituationEventBean>() {
      private SituationEventBean[] EMPTY_ARRAY = new SituationEventBean[0];
      public SituationEventBean[] getEmptyArray() {
        return EMPTY_ARRAY;
      }
      public SituationEventBean demarshall(EJValue a0, MarshallingSession a1) {
        EJObject obj = a0.isObject();
        if (obj == null) {
          return null;
        }
        String objId = obj.get("^ObjectID").isString().stringValue();
        if (a1.hasObject(objId)) {
          return a1.getObject(SituationEventBean.class, objId);
        }
        SituationEventBean entity = new SituationEventBean();
        a1.recordObject(objId, entity);
        if ((obj.containsKey("situationId")) && (!obj.get("situationId").isNull())) {
          entity.setSituationId(java_lang_String.demarshall(obj.get("situationId"), a1));
        }
        if ((obj.containsKey("severity")) && (!obj.get("severity").isNull())) {
          entity.setSeverity(java_lang_String.demarshall(obj.get("severity"), a1));
        }
        if ((obj.containsKey("type")) && (!obj.get("type").isNull())) {
          entity.setType(java_lang_String.demarshall(obj.get("type"), a1));
        }
        if ((obj.containsKey("subject")) && (!obj.get("subject").isNull())) {
          entity.setSubject(java_lang_String.demarshall(obj.get("subject"), a1));
        }
        if ((obj.containsKey("timestamp")) && (!obj.get("timestamp").isNull())) {
          entity.setTimestamp(java_util_Date.demarshall(obj.get("timestamp"), a1));
        }
        return entity;
      }
      public String marshall(SituationEventBean a0, MarshallingSession a1) {
        if (a0 == null) {
          return "null";
        }
        final boolean ref = a1.hasObject(a0);
        final StringBuilder json = new StringBuilder("{\"^EncodedType\":\"org.overlord.rtgov.ui.client.model.SituationEventBean\",\"^ObjectID\"");
        json.append(":\"").append(a1.getObject(a0)).append("\"");
        if (ref) {
          return json.append("}").toString();
        }
        return json.append(",").append("\"situationId\":").append(java_lang_String.marshall(a0.getSituationId(), a1)).append(",").append("\"severity\":").append(java_lang_String.marshall(a0.getSeverity(), a1)).append(",").append("\"type\":").append(java_lang_String.marshall(a0.getType(), a1)).append(",").append("\"subject\":").append(java_lang_String.marshall(a0.getSubject(), a1)).append(",").append("\"timestamp\":").append(java_util_Date.marshall(a0.getTimestamp(), a1)).append("}").toString();
      }
    };
    marshallers.put("org.overlord.rtgov.ui.client.model.SituationEventBean", org_overlord_rtgov_ui_client_model_SituationEventBean);
    org_overlord_rtgov_ui_client_model_SituationResultSetBean = new Marshaller<SituationResultSetBean>() {
      private SituationResultSetBean[] EMPTY_ARRAY = new SituationResultSetBean[0];
      public SituationResultSetBean[] getEmptyArray() {
        return EMPTY_ARRAY;
      }
      public SituationResultSetBean demarshall(EJValue a0, MarshallingSession a1) {
        EJObject obj = a0.isObject();
        if (obj == null) {
          return null;
        }
        String objId = obj.get("^ObjectID").isString().stringValue();
        if (a1.hasObject(objId)) {
          return a1.getObject(SituationResultSetBean.class, objId);
        }
        SituationResultSetBean entity = new SituationResultSetBean();
        a1.recordObject(objId, entity);
        if ((obj.containsKey("situations")) && (!obj.get("situations").isNull())) {
          a1.setAssumedElementType("org.overlord.rtgov.ui.client.model.SituationSummaryBean");
          entity.setSituations(java_util_List.demarshall(obj.get("situations"), a1));
        }
        if ((obj.containsKey("totalResults")) && (!obj.get("totalResults").isNull())) {
          entity.setTotalResults(java_lang_Long.demarshall(obj.get("totalResults"), a1));
        }
        if ((obj.containsKey("itemsPerPage")) && (!obj.get("itemsPerPage").isNull())) {
          entity.setItemsPerPage(java_lang_Integer.demarshall(obj.get("itemsPerPage"), a1));
        }
        if ((obj.containsKey("startIndex")) && (!obj.get("startIndex").isNull())) {
          entity.setStartIndex(java_lang_Integer.demarshall(obj.get("startIndex"), a1));
        }
        return entity;
      }
      public String marshall(SituationResultSetBean a0, MarshallingSession a1) {
        if (a0 == null) {
          return "null";
        }
        final boolean ref = a1.hasObject(a0);
        final StringBuilder json = new StringBuilder("{\"^EncodedType\":\"org.overlord.rtgov.ui.client.model.SituationResultSetBean\",\"^ObjectID\"");
        json.append(":\"").append(a1.getObject(a0)).append("\"");
        if (ref) {
          return json.append("}").toString();
        }
        return json.append(",").append("\"situations\":").append(java_util_List.marshall(a0.getSituations(), a1)).append(",").append("\"totalResults\":").append(java_lang_Long.marshall(a0.getTotalResults(), a1)).append(",").append("\"itemsPerPage\":").append(java_lang_Integer.marshall(a0.getItemsPerPage(), a1)).append(",").append("\"startIndex\":").append(java_lang_Integer.marshall(a0.getStartIndex(), a1)).append("}").toString();
      }
    };
    marshallers.put("org.overlord.rtgov.ui.client.model.SituationResultSetBean", org_overlord_rtgov_ui_client_model_SituationResultSetBean);
    org_overlord_rtgov_ui_client_model_ComponentServiceSummaryBean = new Marshaller<ComponentServiceSummaryBean>() {
      private ComponentServiceSummaryBean[] EMPTY_ARRAY = new ComponentServiceSummaryBean[0];
      public ComponentServiceSummaryBean[] getEmptyArray() {
        return EMPTY_ARRAY;
      }
      public ComponentServiceSummaryBean demarshall(EJValue a0, MarshallingSession a1) {
        EJObject obj = a0.isObject();
        if (obj == null) {
          return null;
        }
        String objId = obj.get("^ObjectID").isString().stringValue();
        if (a1.hasObject(objId)) {
          return a1.getObject(ComponentServiceSummaryBean.class, objId);
        }
        ComponentServiceSummaryBean entity = new ComponentServiceSummaryBean();
        a1.recordObject(objId, entity);
        if ((obj.containsKey("serviceId")) && (!obj.get("serviceId").isNull())) {
          entity.setServiceId(java_lang_String.demarshall(obj.get("serviceId"), a1));
        }
        if ((obj.containsKey("name")) && (!obj.get("name").isNull())) {
          entity.setName(java_lang_String.demarshall(obj.get("name"), a1));
        }
        if ((obj.containsKey("application")) && (!obj.get("application").isNull())) {
          entity.setApplication(java_lang_String.demarshall(obj.get("application"), a1));
        }
        if ((obj.containsKey("iface")) && (!obj.get("iface").isNull())) {
          entity.setIface(java_lang_String.demarshall(obj.get("iface"), a1));
        }
        if ((obj.containsKey("implementation")) && (!obj.get("implementation").isNull())) {
          entity.setImplementation(java_lang_String.demarshall(obj.get("implementation"), a1));
        }
        return entity;
      }
      public String marshall(ComponentServiceSummaryBean a0, MarshallingSession a1) {
        if (a0 == null) {
          return "null";
        }
        final boolean ref = a1.hasObject(a0);
        final StringBuilder json = new StringBuilder("{\"^EncodedType\":\"org.overlord.rtgov.ui.client.model.ComponentServiceSummaryBean\",\"^ObjectID\"");
        json.append(":\"").append(a1.getObject(a0)).append("\"");
        if (ref) {
          return json.append("}").toString();
        }
        return json.append(",").append("\"serviceId\":").append(java_lang_String.marshall(a0.getServiceId(), a1)).append(",").append("\"name\":").append(java_lang_String.marshall(a0.getName(), a1)).append(",").append("\"application\":").append(java_lang_String.marshall(a0.getApplication(), a1)).append(",").append("\"iface\":").append(java_lang_String.marshall(a0.getIface(), a1)).append(",").append("\"implementation\":").append(java_lang_String.marshall(a0.getImplementation(), a1)).append("}").toString();
      }
    };
    marshallers.put("org.overlord.rtgov.ui.client.model.ComponentServiceSummaryBean", org_overlord_rtgov_ui_client_model_ComponentServiceSummaryBean);
    org_overlord_rtgov_ui_client_model_BatchRetryResult = new Marshaller<BatchRetryResult>() {
      private BatchRetryResult[] EMPTY_ARRAY = new BatchRetryResult[0];
      public BatchRetryResult[] getEmptyArray() {
        return EMPTY_ARRAY;
      }
      public BatchRetryResult demarshall(EJValue a0, MarshallingSession a1) {
        EJObject obj = a0.isObject();
        if (obj == null) {
          return null;
        }
        String objId = obj.get("^ObjectID").isString().stringValue();
        if (a1.hasObject(objId)) {
          return a1.getObject(BatchRetryResult.class, objId);
        }
        BatchRetryResult entity = new BatchRetryResult();
        a1.recordObject(objId, entity);
        if ((obj.containsKey("processedCount")) && (!obj.get("processedCount").isNull())) {
          entity.setProcessedCount(java_lang_Integer.demarshall(obj.get("processedCount"), a1));
        }
        if ((obj.containsKey("failedCount")) && (!obj.get("failedCount").isNull())) {
          entity.setFailedCount(java_lang_Integer.demarshall(obj.get("failedCount"), a1));
        }
        if ((obj.containsKey("ignoredCount")) && (!obj.get("ignoredCount").isNull())) {
          entity.setIgnoredCount(java_lang_Integer.demarshall(obj.get("ignoredCount"), a1));
        }
        return entity;
      }
      public String marshall(BatchRetryResult a0, MarshallingSession a1) {
        if (a0 == null) {
          return "null";
        }
        final boolean ref = a1.hasObject(a0);
        final StringBuilder json = new StringBuilder("{\"^EncodedType\":\"org.overlord.rtgov.ui.client.model.BatchRetryResult\",\"^ObjectID\"");
        json.append(":\"").append(a1.getObject(a0)).append("\"");
        if (ref) {
          return json.append("}").toString();
        }
        return json.append(",").append("\"processedCount\":").append(java_lang_Integer.marshall(a0.getProcessedCount(), a1)).append(",").append("\"failedCount\":").append(java_lang_Integer.marshall(a0.getFailedCount(), a1)).append(",").append("\"ignoredCount\":").append(java_lang_Integer.marshall(a0.getIgnoredCount(), a1)).append("}").toString();
      }
    };
    marshallers.put("org.overlord.rtgov.ui.client.model.BatchRetryResult", org_overlord_rtgov_ui_client_model_BatchRetryResult);
    org_overlord_rtgov_ui_client_model_ReferenceSummaryBean = new Marshaller<ReferenceSummaryBean>() {
      private ReferenceSummaryBean[] EMPTY_ARRAY = new ReferenceSummaryBean[0];
      public ReferenceSummaryBean[] getEmptyArray() {
        return EMPTY_ARRAY;
      }
      public ReferenceSummaryBean demarshall(EJValue a0, MarshallingSession a1) {
        EJObject obj = a0.isObject();
        if (obj == null) {
          return null;
        }
        String objId = obj.get("^ObjectID").isString().stringValue();
        if (a1.hasObject(objId)) {
          return a1.getObject(ReferenceSummaryBean.class, objId);
        }
        ReferenceSummaryBean entity = new ReferenceSummaryBean();
        a1.recordObject(objId, entity);
        if ((obj.containsKey("referenceId")) && (!obj.get("referenceId").isNull())) {
          entity.setReferenceId(java_lang_String.demarshall(obj.get("referenceId"), a1));
        }
        if ((obj.containsKey("name")) && (!obj.get("name").isNull())) {
          entity.setName(java_lang_String.demarshall(obj.get("name"), a1));
        }
        if ((obj.containsKey("application")) && (!obj.get("application").isNull())) {
          entity.setApplication(java_lang_String.demarshall(obj.get("application"), a1));
        }
        if ((obj.containsKey("iface")) && (!obj.get("iface").isNull())) {
          entity.setIface(java_lang_String.demarshall(obj.get("iface"), a1));
        }
        if ((obj.containsKey("bindings")) && (!obj.get("bindings").isNull())) {
          entity.setBindings(java_lang_String.demarshall(obj.get("bindings"), a1));
        }
        return entity;
      }
      public String marshall(ReferenceSummaryBean a0, MarshallingSession a1) {
        if (a0 == null) {
          return "null";
        }
        final boolean ref = a1.hasObject(a0);
        final StringBuilder json = new StringBuilder("{\"^EncodedType\":\"org.overlord.rtgov.ui.client.model.ReferenceSummaryBean\",\"^ObjectID\"");
        json.append(":\"").append(a1.getObject(a0)).append("\"");
        if (ref) {
          return json.append("}").toString();
        }
        return json.append(",").append("\"referenceId\":").append(java_lang_String.marshall(a0.getReferenceId(), a1)).append(",").append("\"name\":").append(java_lang_String.marshall(a0.getName(), a1)).append(",").append("\"application\":").append(java_lang_String.marshall(a0.getApplication(), a1)).append(",").append("\"iface\":").append(java_lang_String.marshall(a0.getIface(), a1)).append(",").append("\"bindings\":").append(java_lang_String.marshall(a0.getBindings(), a1)).append("}").toString();
      }
    };
    marshallers.put("org.overlord.rtgov.ui.client.model.ReferenceSummaryBean", org_overlord_rtgov_ui_client_model_ReferenceSummaryBean);
    org_overlord_rtgov_ui_client_model_ServiceResultSetBean = new Marshaller<ServiceResultSetBean>() {
      private ServiceResultSetBean[] EMPTY_ARRAY = new ServiceResultSetBean[0];
      public ServiceResultSetBean[] getEmptyArray() {
        return EMPTY_ARRAY;
      }
      public ServiceResultSetBean demarshall(EJValue a0, MarshallingSession a1) {
        EJObject obj = a0.isObject();
        if (obj == null) {
          return null;
        }
        String objId = obj.get("^ObjectID").isString().stringValue();
        if (a1.hasObject(objId)) {
          return a1.getObject(ServiceResultSetBean.class, objId);
        }
        ServiceResultSetBean entity = new ServiceResultSetBean();
        a1.recordObject(objId, entity);
        if ((obj.containsKey("services")) && (!obj.get("services").isNull())) {
          a1.setAssumedElementType("org.overlord.rtgov.ui.client.model.ServiceSummaryBean");
          entity.setServices(java_util_List.demarshall(obj.get("services"), a1));
        }
        if ((obj.containsKey("totalResults")) && (!obj.get("totalResults").isNull())) {
          entity.setTotalResults(java_lang_Long.demarshall(obj.get("totalResults"), a1));
        }
        if ((obj.containsKey("itemsPerPage")) && (!obj.get("itemsPerPage").isNull())) {
          entity.setItemsPerPage(java_lang_Integer.demarshall(obj.get("itemsPerPage"), a1));
        }
        if ((obj.containsKey("startIndex")) && (!obj.get("startIndex").isNull())) {
          entity.setStartIndex(java_lang_Integer.demarshall(obj.get("startIndex"), a1));
        }
        return entity;
      }
      public String marshall(ServiceResultSetBean a0, MarshallingSession a1) {
        if (a0 == null) {
          return "null";
        }
        final boolean ref = a1.hasObject(a0);
        final StringBuilder json = new StringBuilder("{\"^EncodedType\":\"org.overlord.rtgov.ui.client.model.ServiceResultSetBean\",\"^ObjectID\"");
        json.append(":\"").append(a1.getObject(a0)).append("\"");
        if (ref) {
          return json.append("}").toString();
        }
        return json.append(",").append("\"services\":").append(java_util_List.marshall(a0.getServices(), a1)).append(",").append("\"totalResults\":").append(java_lang_Long.marshall(a0.getTotalResults(), a1)).append(",").append("\"itemsPerPage\":").append(java_lang_Integer.marshall(a0.getItemsPerPage(), a1)).append(",").append("\"startIndex\":").append(java_lang_Integer.marshall(a0.getStartIndex(), a1)).append("}").toString();
      }
    };
    marshallers.put("org.overlord.rtgov.ui.client.model.ServiceResultSetBean", org_overlord_rtgov_ui_client_model_ServiceResultSetBean);
    org_overlord_rtgov_ui_client_model_SituationBean = new Marshaller<SituationBean>() {
      private SituationBean[] EMPTY_ARRAY = new SituationBean[0];
      public SituationBean[] getEmptyArray() {
        return EMPTY_ARRAY;
      }
      public SituationBean demarshall(EJValue a0, MarshallingSession a1) {
        EJObject obj = a0.isObject();
        if (obj == null) {
          return null;
        }
        String objId = obj.get("^ObjectID").isString().stringValue();
        if (a1.hasObject(objId)) {
          return a1.getObject(SituationBean.class, objId);
        }
        SituationBean entity = new SituationBean();
        a1.recordObject(objId, entity);
        if ((obj.containsKey("context")) && (!obj.get("context").isNull())) {
          a1.setAssumedMapKeyType("java.lang.String");
          a1.setAssumedMapValueType("java.lang.String");
          entity.setContext((Map) java_util_Map.demarshall(obj.get("context"), a1));
          a1.resetAssumedTypes();
        }
        if ((obj.containsKey("message")) && (!obj.get("message").isNull())) {
          entity.setMessage(org_overlord_rtgov_ui_client_model_MessageBean.demarshall(obj.get("message"), a1));
        }
        if ((obj.containsKey("callTrace")) && (!obj.get("callTrace").isNull())) {
          entity.setCallTrace(org_overlord_rtgov_ui_client_model_CallTraceBean.demarshall(obj.get("callTrace"), a1));
        }
        if ((obj.containsKey("isAssignedToCurrentUser")) && (!obj.get("isAssignedToCurrentUser").isNull())) {
          _$1865349277__64711720_isAssignedToCurrentUser(entity, java_lang_Boolean.demarshall(obj.get("isAssignedToCurrentUser"), a1));
        }
        if ((obj.containsKey("isTakeoverPossible")) && (!obj.get("isTakeoverPossible").isNull())) {
          _$1865349277__64711720_isTakeoverPossible(entity, java_lang_Boolean.demarshall(obj.get("isTakeoverPossible"), a1));
        }
        if ((obj.containsKey("isResubmitPossible")) && (!obj.get("isResubmitPossible").isNull())) {
          _$1865349277__64711720_isResubmitPossible(entity, java_lang_Boolean.demarshall(obj.get("isResubmitPossible"), a1));
        }
        if ((obj.containsKey("situationId")) && (!obj.get("situationId").isNull())) {
          entity.setSituationId(java_lang_String.demarshall(obj.get("situationId"), a1));
        }
        if ((obj.containsKey("severity")) && (!obj.get("severity").isNull())) {
          entity.setSeverity(java_lang_String.demarshall(obj.get("severity"), a1));
        }
        if ((obj.containsKey("type")) && (!obj.get("type").isNull())) {
          entity.setType(java_lang_String.demarshall(obj.get("type"), a1));
        }
        if ((obj.containsKey("subject")) && (!obj.get("subject").isNull())) {
          entity.setSubject(java_lang_String.demarshall(obj.get("subject"), a1));
        }
        if ((obj.containsKey("timestamp")) && (!obj.get("timestamp").isNull())) {
          entity.setTimestamp(java_util_Date.demarshall(obj.get("timestamp"), a1));
        }
        if ((obj.containsKey("description")) && (!obj.get("description").isNull())) {
          entity.setDescription(java_lang_String.demarshall(obj.get("description"), a1));
        }
        if ((obj.containsKey("properties")) && (!obj.get("properties").isNull())) {
          a1.setAssumedMapKeyType("java.lang.String");
          a1.setAssumedMapValueType("java.lang.String");
          entity.setProperties((Map) java_util_Map.demarshall(obj.get("properties"), a1));
          a1.resetAssumedTypes();
        }
        return entity;
      }
      public String marshall(SituationBean a0, MarshallingSession a1) {
        if (a0 == null) {
          return "null";
        }
        final boolean ref = a1.hasObject(a0);
        final StringBuilder json = new StringBuilder("{\"^EncodedType\":\"org.overlord.rtgov.ui.client.model.SituationBean\",\"^ObjectID\"");
        json.append(":\"").append(a1.getObject(a0)).append("\"");
        if (ref) {
          return json.append("}").toString();
        }
        return json.append(",").append("\"context\":").append(java_util_Map.marshall(a0.getContext(), a1)).append(",").append("\"message\":").append(org_overlord_rtgov_ui_client_model_MessageBean.marshall(a0.getMessage(), a1)).append(",").append("\"callTrace\":").append(org_overlord_rtgov_ui_client_model_CallTraceBean.marshall(a0.getCallTrace(), a1)).append(",").append("\"isAssignedToCurrentUser\":").append(java_lang_Boolean.marshall(_$1865349277__64711720_isAssignedToCurrentUser(a0), a1)).append(",").append("\"isTakeoverPossible\":").append(java_lang_Boolean.marshall(_$1865349277__64711720_isTakeoverPossible(a0), a1)).append(",").append("\"isResubmitPossible\":").append(java_lang_Boolean.marshall(_$1865349277__64711720_isResubmitPossible(a0), a1)).append(",").append("\"situationId\":").append(java_lang_String.marshall(a0.getSituationId(), a1)).append(",").append("\"severity\":").append(java_lang_String.marshall(a0.getSeverity(), a1)).append(",").append("\"type\":").append(java_lang_String.marshall(a0.getType(), a1)).append(",").append("\"subject\":").append(java_lang_String.marshall(a0.getSubject(), a1)).append(",").append("\"timestamp\":").append(java_util_Date.marshall(a0.getTimestamp(), a1)).append(",").append("\"description\":").append(java_lang_String.marshall(a0.getDescription(), a1)).append(",").append("\"properties\":").append(java_util_Map.marshall(a0.getProperties(), a1)).append("}").toString();
      }
    };
    marshallers.put("org.overlord.rtgov.ui.client.model.SituationBean", org_overlord_rtgov_ui_client_model_SituationBean);
    org_overlord_rtgov_ui_client_model_NotificationType = new Marshaller<NotificationType>() {
      private NotificationType[] EMPTY_ARRAY = new NotificationType[0];
      public NotificationType[] getEmptyArray() {
        return EMPTY_ARRAY;
      }
      public NotificationType demarshall(EJValue a0, MarshallingSession a1) {
        EJObject obj = a0.isObject();
        NotificationType entity = obj != null ? Enum.valueOf(NotificationType.class, obj.get("^EnumStringValue").isString().stringValue()) : a0.isString() != null ? Enum.valueOf(NotificationType.class, a0.isString().stringValue()) : null;
        return entity;
      }
      public String marshall(NotificationType a0, MarshallingSession a1) {
        if (a0 == null) {
          return "null";
        }
        final StringBuilder json = new StringBuilder();
        return json.append(a0 != null ? new StringBuilder(64).append("{\"^EncodedType\":\"org.overlord.rtgov.ui.client.model.NotificationType\",\"^EnumStringValue\":\"").append(a0.name()).append("\"}") : "null").toString();
      }
    };
    marshallers.put("org.overlord.rtgov.ui.client.model.NotificationType", org_overlord_rtgov_ui_client_model_NotificationType);
  }

  private native static String _1193124396__1195259493__content(MessageBean instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.model.MessageBean::_content;
  }-*/;

  private native static void _1193124396__1195259493__content(MessageBean instance, String value) /*-{
    instance.@org.overlord.rtgov.ui.client.model.MessageBean::_content = value;
  }-*/;

  private native static Date _1968081542__65575278_date(NotificationBean instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.model.NotificationBean::date;
  }-*/;

  private native static void _1968081542__65575278_date(NotificationBean instance, Date value) /*-{
    instance.@org.overlord.rtgov.ui.client.model.NotificationBean::date = value;
  }-*/;

  private native static String _1630335596__1195259493_detailMessage(Throwable instance) /*-{
    return instance.@java.lang.Throwable::detailMessage;
  }-*/;

  private native static void _1630335596__1195259493_detailMessage(Throwable instance, String value) /*-{
    instance.@java.lang.Throwable::detailMessage = value;
  }-*/;

  private native static boolean _$1865349277__64711720_isAssignedToCurrentUser(SituationBean instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.model.SituationBean::isAssignedToCurrentUser;
  }-*/;

  private native static void _$1865349277__64711720_isAssignedToCurrentUser(SituationBean instance, boolean value) /*-{
    instance.@org.overlord.rtgov.ui.client.model.SituationBean::isAssignedToCurrentUser = value;
  }-*/;

  private native static boolean _$1865349277__64711720_isTakeoverPossible(SituationBean instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.model.SituationBean::isTakeoverPossible;
  }-*/;

  private native static void _$1865349277__64711720_isTakeoverPossible(SituationBean instance, boolean value) /*-{
    instance.@org.overlord.rtgov.ui.client.model.SituationBean::isTakeoverPossible = value;
  }-*/;

  private native static boolean _$1865349277__64711720_isResubmitPossible(SituationBean instance) /*-{
    return instance.@org.overlord.rtgov.ui.client.model.SituationBean::isResubmitPossible;
  }-*/;

  private native static void _$1865349277__64711720_isResubmitPossible(SituationBean instance, boolean value) /*-{
    instance.@org.overlord.rtgov.ui.client.model.SituationBean::isResubmitPossible = value;
  }-*/;

  public Marshaller getMarshaller(String a0, String a1) {
    return marshallers.get(a1);
  }
}