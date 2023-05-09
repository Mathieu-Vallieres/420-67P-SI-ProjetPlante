#include "LinkedList.h"

template <class T, class U>
class Dictionary
{
private:
    LinkedList<T> KeyList = LinkedList<T>();
    LinkedList<U> ValList = LinkedList<U>();

public:
    LinkedList<T> GetKeys() {
      return KeyList;
    }

    void set(T key, U val)
    {
        for (int i = 0; i < KeyList.size(); i++)
        {
            if (KeyList.get(i) == key) {
                ValList.set(i, val);
                return;
            }
        }
    }
    void add(T key, U val)
    {
        for (int i = 0; i < KeyList.size(); i++)
        {
            if (KeyList.get(i) == key) {
                return;
            }
        }
        KeyList.add(key);
        ValList.add(val);
    }

    void remove(T key) {
      int pos = 0;
      for (int i = 0; i < KeyList.size(); i++)
        {
            if (KeyList.get(i) == key)
            {
                // Alors on récupère la position
                pos = i;
            }
        }

      KeyList.remove(pos);
      ValList.remove(pos);
    }

    U get(T key)
    {
        for (int i = 0; i < KeyList.size(); i++)
        {
            if (KeyList.get(i) == key)
            {
                return ValList.get(i);
            }
        }
    }

    T getKey(U val)
    {
        for (int i = 0; i < ValList.size(); i++)
        {
            if (ValList.get(i) == val)
            {
                return KeyList.get(i);
            }
        }
    }

    int length()
    {
        return KeyList.size();
    }
};
