package it.polimi.kundera.client.azuretable.tests;

import com.impetus.kundera.KunderaException;
import it.polimi.kundera.client.azuretable.AzureTableKey;
import it.polimi.kundera.client.azuretable.entities.Phone;
import it.polimi.kundera.client.azuretable.entities.PhoneInvalid1;
import it.polimi.kundera.client.azuretable.entities.PhoneInvalid2;
import it.polimi.kundera.client.azuretable.entities.PhoneString;
import org.junit.Assert;
import org.junit.Test;

import javax.persistence.TypedQuery;

/**
 * @author Fabio Arcidiacono.
 */
public class IdsTest extends TestBase {

    @Test
    public void autoGeneratedIdTest() {
        print("auto generated id");
        Phone phone = new Phone();
        phone.setNumber(123456789L);
        em.persist(phone);
        Assert.assertNotNull(phone.getId());

        String phnId = phone.getId();
        clear();

        print("read");
        Phone foundPhone = em.find(Phone.class, phnId);
        Assert.assertNotNull(foundPhone);
        Assert.assertEquals(phnId, foundPhone.getId());
        Assert.assertEquals((Long) 123456789L, foundPhone.getNumber());

        print("update");
        foundPhone.setNumber(987654321L);
        em.merge(foundPhone);

        clear();

        TypedQuery<Phone> query = em.createQuery("SELECT p FROM Phone p WHERE p.id = :id", Phone.class);
        foundPhone = query.setParameter("id", phnId).getSingleResult();
        Assert.assertNotNull(foundPhone);
        Assert.assertEquals(phnId, foundPhone.getId());
        Assert.assertEquals((Long) 987654321L, foundPhone.getNumber());

        print("delete");
        em.remove(foundPhone);
        foundPhone = em.find(Phone.class, phnId);
        Assert.assertNull(foundPhone);
    }

    @Test
    public void userFullStringIdTest() {
        print("user full string id with utils");
        String id = AzureTableKey.asString("1", "phone");
        PhoneString phone = new PhoneString();
        phone.setNumber(123456789L);
        phone.setId(id);
        em.persist(phone);
        Assert.assertNotNull(phone.getId());
        // ID should be "partitionKey_rowKey" and both are user specified
        Assert.assertEquals("1_phone", phone.getId());

        clear();

        print("read");
        PhoneString foundPhone = em.find(PhoneString.class, id);
        Assert.assertNotNull(foundPhone);
        Assert.assertEquals(id, foundPhone.getId());
        Assert.assertEquals((Long) 123456789L, foundPhone.getNumber());

        print("update");
        foundPhone.setNumber(987654321L);
        em.merge(foundPhone);

        clear();

        TypedQuery<PhoneString> query = em.createQuery("SELECT p FROM PhoneString p WHERE p.id = :id", PhoneString.class);
        foundPhone = query.setParameter("id", id).getSingleResult();
        Assert.assertNotNull(foundPhone);
        Assert.assertEquals(id, foundPhone.getId());
        Assert.assertEquals((Long) 987654321L, foundPhone.getNumber());

        print("delete");
        em.remove(foundPhone);
        foundPhone = em.find(PhoneString.class, id);
        Assert.assertNull(foundPhone);

        print("user full string id without utils");
        id = "123456_phone1";
        phone = new PhoneString();
        phone.setNumber(123456789L);
        phone.setId(id);
        em.persist(phone);
        Assert.assertNotNull(phone.getId());
        // ID should be "partitionKey_rowKey" and both are user specified
        Assert.assertEquals("123456_phone1", phone.getId());

        clear();

        print("read");
        foundPhone = em.find(PhoneString.class, id);
        Assert.assertNotNull(foundPhone);
        Assert.assertEquals(id, foundPhone.getId());
        Assert.assertEquals((Long) 123456789L, foundPhone.getNumber());

        print("update");
        foundPhone.setNumber(987654321L);
        em.merge(foundPhone);

        clear();

        query = em.createQuery("SELECT p FROM PhoneString p WHERE p.id = :id", PhoneString.class);
        foundPhone = query.setParameter("id", id).getSingleResult();
        Assert.assertNotNull(foundPhone);
        Assert.assertEquals(id, foundPhone.getId());
        Assert.assertEquals((Long) 987654321L, foundPhone.getNumber());

        print("delete");
        em.remove(foundPhone);
        foundPhone = em.find(PhoneString.class, id);
        Assert.assertNull(foundPhone);
    }

    @Test
    public void testIdWithoutPartitionKey() {
        print("user row key with utils");
        String id = AzureTableKey.asString("phone");
        PhoneString phone = new PhoneString();
        phone.setNumber(123456789L);
        phone.setId(id);
        em.persist(phone);
        Assert.assertNotNull(phone.getId());
        // ID should be "rowKey", partition key is implicitly the default one
        Assert.assertEquals("phone", phone.getId());

        clear();

        print("read");
        PhoneString foundPhone = em.find(PhoneString.class, id);
        Assert.assertNotNull(foundPhone);
        Assert.assertEquals(id, foundPhone.getId());
        Assert.assertEquals((Long) 123456789L, foundPhone.getNumber());

        print("update");
        foundPhone.setNumber(987654321L);
        em.merge(foundPhone);

        clear();

        TypedQuery<PhoneString> query = em.createQuery("SELECT p FROM PhoneString p WHERE p.id = :id", PhoneString.class);
        foundPhone = query.setParameter("id", id).getSingleResult();
        Assert.assertNotNull(foundPhone);
        Assert.assertEquals(id, foundPhone.getId());
        Assert.assertEquals((Long) 987654321L, foundPhone.getNumber());

        print("user rowKey without utils");
        id = "phone1";
        phone = new PhoneString();
        phone.setNumber(123456789L);
        phone.setId(id);
        em.persist(phone);
        Assert.assertNotNull(phone.getId());
        // ID should be "rowKey", partition key is implicitly the default one
        Assert.assertEquals("phone1", phone.getId());

        clear();

        print("read");
        foundPhone = em.find(PhoneString.class, id);
        Assert.assertNotNull(foundPhone);
        Assert.assertEquals(id, foundPhone.getId());
        Assert.assertEquals((Long) 123456789L, foundPhone.getNumber());

        print("update");
        foundPhone.setNumber(987654321L);
        em.merge(foundPhone);

        clear();

        query = em.createQuery("SELECT p FROM PhoneString p WHERE p.id = :id", PhoneString.class);
        foundPhone = query.setParameter("id", id).getSingleResult();
        Assert.assertNotNull(foundPhone);
        Assert.assertEquals(id, foundPhone.getId());
        Assert.assertEquals((Long) 987654321L, foundPhone.getNumber());

        print("delete");
        em.remove(foundPhone);
        foundPhone = em.find(PhoneString.class, id);
        Assert.assertNull(foundPhone);
    }

    @Test
    public void invalidIdTypeTest() {
        print("invalid double id");
        // ID should be String
        PhoneInvalid1 phone = new PhoneInvalid1();
        phone.setNumber(123456789L);
        phone.setId(1D);
        thrown.expect(KunderaException.class);
        em.persist(phone);

        print("cleanup");
        em.remove(phone);
    }

    @Test
    public void invalidAutoGeneratedIdTypeTest() {
        print("invalid auto generated id");
        // ID should be String also if annotated with @GeneratedValue
        PhoneInvalid2 phone = new PhoneInvalid2();
        phone.setNumber(123456789L);
        phone.setId(1D);
        thrown.expect(KunderaException.class);
        em.persist(phone);

        print("cleanup");
        em.remove(phone);
    }
}
