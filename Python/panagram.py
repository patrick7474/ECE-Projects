def check(a):
    alphabet="abcdefghijklmnopqrstuvwxyz"
    for char in alphabet:
        if char not in a.lower():
            return False
    return True
a= input("enter a sentence")
if(check(a) == True):
    print("the entered sentence is a panagram")
else:
    print("the entered sentence is not a panagram")
