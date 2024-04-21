package northshorefiend.clean;

import northshorefiend.model.Foo;

import java.util.Optional;

public class FooService {

    /**
     * @param id   id of Foo to update
     * @param name name to set
     * @throws FooException on failure
     */
    public Foo upsertFoo(long id, String name) throws FooException {
        String token = login();

        Optional<Foo> optionalFoo = findFoo(token, id);

        if (optionalFoo.isEmpty()) {
            return insertFoo(token, new Foo(id, name));
        }

        Foo existingFoo = optionalFoo.get();
        existingFoo.setName(name);
        return updateFoo(token, existingFoo);
    }

    /**
     * @return Authentication token,
     * @throws FooException if fails to authenticate
     */
    private String login() throws FooException {
        return null;
    }

    /**
     * @param token Authentication token
     * @param id    id of Foo to find
     * @return Foo with given {@param id} or empty
     * @throws FooException on failure
     */
    private Optional<Foo> findFoo(String token, long id) throws FooException {
        return Optional.empty();
    }

    /**
     * @param token authentication token
     * @param foo   Foo to insert
     * @throws FooException on failure
     */
    private Foo insertFoo(String token, Foo foo) throws FooException{
        return foo;
    }

    /**
     * @param token authentication token
     * @param foo   Foo to update
     * @throws FooException on failure
     */
    private Foo updateFoo(String token, Foo foo) throws FooException {
        return foo;
    }
}