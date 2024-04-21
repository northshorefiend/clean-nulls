package northshorefiend.unclean;

import northshorefiend.model.Foo;

public class FooService {

    /**
     * @param id   id of Foo to update
     * @param name name to set
     * @return true if successfully updated/inserted
     */
    public boolean upsertFoo(long id, String name) {
        String token = login();

        if (token != null) {
            Foo foo = findFoo(token, id);
            if (foo == null) {
                foo = new Foo(id, name);
                return insertFoo(token, foo);
            } else {
                foo.setName(name);
                return updateFoo(token, foo);
            }
        } else {
            return false;
        }
    }

    /**
     * @return Authentication token, null if failed
     */
    private String login() {
        // return token or null if failed to login
        return null;
    }

    /**
     * @param token Authentication token
     * @param id    id of Foo to find
     * @return Foo with id {@param id} or null if not found
     */
    private Foo findFoo(String token, long id) {
        return null;
    }

    /**
     * @param token authentication token
     * @param foo   Foo to update
     * @return true if successfully updated
     */
    private boolean updateFoo(String token, Foo foo) {
        return false;
    }

    /**
     * @param token authentication token
     * @param foo   Foo to insert
     * @return true if successfully inserted
     */
    private boolean insertFoo(String token, Foo foo) {
        // returns true if save succeeds
        return false;
    }
}