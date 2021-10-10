#include <iostream>
#include "../Include/Watchable.h"
#include "../Include/User.h"
#include "../Include/Action.h"
#include "../Include/Session.h"
#include "../Include/Watchable.h"

using namespace std;

int main(int argc, char** argv){
	if(argc!=2)
	{
		cout << "usage splflix input_file" << endl;
		return 0;
	}

	Session s(argv[1]);
	s.start();
	return 0;
}


