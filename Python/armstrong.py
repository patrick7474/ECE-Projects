a=int(input("enter a positive number"))
length=len(str(a))
temp=a
sum=0
while temp>0:
    d=temp%10
    sum+=d**length
    temp=temp//10
print(sum)
if sum==a:
    print("it is an armstrong")
else:
    print("it is not an armstrong ")
a=1
b=1
c=a+b
print(c)
    
     
