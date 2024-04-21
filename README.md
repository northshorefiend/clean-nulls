# Cleaning Nulls

Recently, during a Java code review, I came across some code that, I thought, needed refactoring to follow
[Clean Code](https://www.amazon.co.uk/Clean-Code-Handbook-Software-Craftsmanship/dp/0132350882). Here's a
before and after and some commentary.

## Before

    /**
    * @param id   id of Foo to update
    * @param name name to set
    * @return true if successfully updated/inserted
    **/
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

There are a couple of things that straight away I don't like. Firstly, I always find nested ``if``s to be suspect.
Usually, there is a better way to structure your code. Secondly, ``else`` statements are also suspect, again,
there is usually a way to avoid them. Thirdly, from this code we can see that the ``login()``
and ``findFoo(token, id)`` methods return ``null`` when login fails or find fails respectively. It is this ``null``
returning that is causing most of the unclean code. Let's look at a better way.

## Cleaning up

Let's take the ``null`` returning first. What does returning ``null`` mean? The fact that this isn't immediately
obvious is a good reason to find a better way. A ``null`` value would be returned during an exceptional case:
when login fails. This is a clue to what to do, we should assume that the login succeeds and when it doesn't throw an
exception. (I'm going to leave the argument on whether you should use checked or unchecked exceptions for another day)

    public boolean upsertFoo(long id, String name) throws FooException {
        String token = login();
    
        Foo foo = findFoo(token, id);
    
        if (foo == null) {
            foo = new Foo(id, name);
            return insertFoo(token, foo);
        }
    
        foo.setName(name);
        return updateFoo(token, foo);
    }

With just this improvement we have got rid of the nested ``if`` and the ``else``, great!

Now, let's look at ``findFoo``. As this returns null, we have to make sure everytime we call it that we check to see if
we are given a null. Should we throw an exception in the case foo is not found? Well, is it an exceptional case? Not
really, in the normal running of the code we expect that sometimes we will have to create the ``Foo``. Exceptions are
not very efficient, I have seen an application server brought to its knees by some code that was throwing thousands of
exceptions. So, what else do we have in Java that might be an alternative?

# Optional

Java 8 brought in ``Optional`` to the language. In this situation, we can use it as an alternative to returning null.
I think people tend to over use ``Optional``, this was especially true when it first came out, but I think this
case is a good use.

Assuming, ``findFoo`` returns an ``Optional<Foo>`` then we can refactor to this:

    public boolean upsertFoo(long id, String name) throws FooException {
        String token = login();
    
        Optional<Foo> optionalFoo = findFoo(token, id);
    
        if (optionalFoo.isEmpty()) {
            Foo newFoo = new Foo(id, name);
            return insertFoo(token, newFoo);
        }
    
        Foo existingFoo = optionalFoo.get();
        existingFoo.setName(name);
        return updateFoo(token, existingFoo);
    }

We seem to have added more lines of code, so why is this better? Well, now the IDE will tell us if we haven't
checked that the ``Foo`` exists. If we try to call ``optionalFoo.get()`` before calling something that checks if
a value is inside, such as ``optionalFoo.isEmpty()``, then we get a warning. Another improvement, is that it is now
obvious for anyone calling ``findFoo`` that it might not return a ``Foo``.

The ``upsertFoo`` method still returns a boolean. We should do the same for the caller as we have done inside this
class and throw an exception when the upsert fails, rather than returning a boolean. So, where previously the
``insertFoo`` and ``updateFoo`` methods returned a boolean stating their success, which would need explaining, let's
assume they succeed and throw an Exception on failure. What should these methods return? I suggest they return the
updated or inserted ``Foo``. The calling function can decide whether it needs it.

# Clean

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

Hopefully, you agree that this is cleaner code, and less likely to introduce bugs in the future.
Compilable code is on [GitHub](https://github.com/northshorefiend/clean-nulls)
